package com.mindhub.Homebanking.Services.Implement;

import com.itextpdf.text.DocumentException;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Repositories.ClientRepository;
import com.mindhub.Homebanking.Repositories.TransactionRepository;
import com.mindhub.Homebanking.Services.AccountService;
import com.mindhub.Homebanking.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImplement implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountService accountService;
    @Autowired
    ClientRepository clientRepository;

    @Override
    public List<Transaction> findByCreatedBetweenDates(Client client, String string, LocalDateTime date, LocalDateTime date2) {
        Account account=accountService.findByNumber(string);
        List<Transaction> list = new ArrayList<>(); for (Transaction e : account.getTransactions()) {
            if (e.getDate().isAfter(date) && e.getDate().isBefore(date2)) {
                list.add(e);
            }
        }
        return list;
    }

    @Override
    public ResponseEntity<Object> transaction(Authentication authentication, double amount, String description, String accountOriginNumber, String destinationAccountNumber) {
        Account originAccount = accountService.findByNumber(accountOriginNumber.toUpperCase());
        Account destinationAccount = accountService.findByNumber(destinationAccountNumber.toUpperCase());

        //Verificar que los parámetros no estén en blanco
        StringBuilder errorMessage = new StringBuilder();
        if (description.isBlank()) {
            errorMessage.append("Description es requerido\n");
        }
        if (accountOriginNumber.isBlank()) {
            errorMessage.append("El número de cuenta es requerido\n");
        }
        if (destinationAccountNumber.isBlank()) {
            errorMessage.append("La cuenta de destino es requerida\n");
        }
        if (amount == 0.0 || Double.isNaN(amount)) {
            errorMessage.append("Monto es requerido y debe ser un número válido\n");
        }

        if (errorMessage.length() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage.toString());
        }

        //Verifica que el monto no sea inválido
        if (amount < 1) {
            return new ResponseEntity<>("Monto inválido", HttpStatus.FORBIDDEN);
        }

        //Verificar que los números de cuenta no sean iguales
        if (accountOriginNumber.equals(destinationAccountNumber)) {
            return new ResponseEntity<>("Los números de cuenta son iguales", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de origen exista
        if (originAccount == null) {
            return new ResponseEntity<>("La cuenta de origen no existe", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de origen pertenece al cliente autenticado
        if (authentication == null || !originAccount.getClient().getEmail().equals(authentication.getName())) {
            return new ResponseEntity<>("La cuenta de origen no te pertenece", HttpStatus.FORBIDDEN);
        }

        //Verificar que exista la cuenta destino
        if (destinationAccount == null) {
            return new ResponseEntity<>("La cuenta destino no existe", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta de origen tenga el monto disponible
        if (originAccount.getBalance() < amount) {
            return new ResponseEntity<>("No posees fondos suficientes para realizar esta transacción", HttpStatus.FORBIDDEN);
        }

        if (!originAccount.isActive()) {
            return new ResponseEntity<>("Esta cuenta está inactiva, no puedes transferir dinero", HttpStatus.FORBIDDEN);
        }
        if (!destinationAccount.isActive()) {
            return new ResponseEntity<>("La cuenta destino está inactiva", HttpStatus.FORBIDDEN);
        }

        Double initialOriginBalanceAccount = originAccount.getBalance() - amount;
        Transaction debitTransaction = new Transaction(TransactionType.DEBITO, -amount, destinationAccountNumber + " " + description, LocalDateTime.now(), initialOriginBalanceAccount, true);
        Double initialDestinBalanceAccount = destinationAccount.getBalance() + amount;
        Transaction creditTransaction = new Transaction(TransactionType.CREDITO, amount, accountOriginNumber + " " + description, LocalDateTime.now(), initialDestinBalanceAccount, true);

        originAccount.addTransaction(debitTransaction);
        destinationAccount.addTransaction(creditTransaction);

        transactionRepository.save(debitTransaction);
        transactionRepository.save(creditTransaction);

        //Actualizar cuentas con los montos correspondientes
        originAccount.setBalance(originAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        //Guardar cuentas actualizadas a través del repositorio de cuentas
        accountService.saveAccount(originAccount);
        accountService.saveAccount(destinationAccount);

        return new ResponseEntity<>("Transacción exitosa", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> ExportingToPDF(HttpServletResponse response, Authentication authentication, String accNumber, String dateIni, String dateEnd) throws DocumentException, IOException {
        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountService.findByNumber(accNumber);

        if (client == null) {
            return new ResponseEntity<>("No eres un cliente", HttpStatus.FORBIDDEN);
        }

        if (account == null) {
            return new ResponseEntity<>("No existe el número de cuenta", HttpStatus.FORBIDDEN);
        }

        if (client.getAccounts()
                .stream()
                .noneMatch(account1 -> account1.getNumber().equals(account.getNumber()))) {
            return new ResponseEntity<>("Esta cuenta no te pertenece", HttpStatus.FORBIDDEN);
        }

        if (dateIni.isBlank()) {
            return new ResponseEntity<>("La fecha de inicio no puede estar vacía", HttpStatus.FORBIDDEN);
        } else if (dateEnd.isBlank()) {
            return new ResponseEntity<>("La fecha de fin no puede estar vacía", HttpStatus.FORBIDDEN);
        }

        response.setContentType("application/pdf");

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=Transactions" + accNumber + ".pdf";
        response.setHeader(headerKey, headerValue);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime dateTimeIni = LocalDateTime.parse(dateIni, formatter);
        LocalDateTime dateTimeEnd = LocalDateTime.parse(dateEnd, formatter);

        List<Transaction> listTransactions = findByCreatedBetweenDates(client, accNumber, dateTimeIni, dateTimeEnd);

        TransactionPDF exporter = new TransactionPDF(listTransactions, account);
        exporter.usePDFExport(response);

        return new ResponseEntity<>("PDF creado con éxito", HttpStatus.CREATED);
    }
}
