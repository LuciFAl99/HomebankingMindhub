package com.mindhub.Homebanking.Controllers;

import com.itextpdf.text.DocumentException;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Services.AccountService;
import com.mindhub.Homebanking.Services.ClientService;
import com.mindhub.Homebanking.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Transactional
@RestController
public class TransactionController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;


    @PostMapping("/api/clients/current/transactions")
    public ResponseEntity<Object> transaction(
            Authentication authentication, @RequestParam double amount, @RequestParam String description,
            @RequestParam String accountOriginNumber, @RequestParam String destinationAccountNumber) {

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

        transactionService.saveTransaction(debitTransaction);
        transactionService.saveTransaction(creditTransaction);

        //Actualizar cuentas con los montos correspondientes
        originAccount.setBalance(originAccount.getBalance() - amount);
        destinationAccount.setBalance(destinationAccount.getBalance() + amount);

        //Guardar cuentas actualizadas a través del repositorio de cuentas
        accountService.saveAccount(originAccount);
        accountService.saveAccount(destinationAccount);

        return new ResponseEntity<>("Transacción exitosa", HttpStatus.CREATED);
    }

    @PostMapping("/api/clients/current/export-pdf")
    public ResponseEntity<Object> ExportingToPDF(HttpServletResponse response, Authentication authentication, @RequestParam String accNumber, @RequestParam String dateIni, @RequestParam String dateEnd) throws DocumentException, IOException {

        Client client = clientService.findByEmail(authentication.getName());
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

        List<Transaction> listTransactions = transactionService.findByCreatedBetweenDates(client, accNumber, dateTimeIni, dateTimeEnd);

        TransactionPDF exporter = new TransactionPDF(listTransactions, account);
        exporter.usePDFExport(response);

        return new ResponseEntity<>("PDF creado con éxito", HttpStatus.CREATED);
    }
}