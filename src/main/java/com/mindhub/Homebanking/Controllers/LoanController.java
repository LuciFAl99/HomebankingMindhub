package com.mindhub.Homebanking.Controllers;


import com.mindhub.Homebanking.Dtos.LoanApplicationDto;
import com.mindhub.Homebanking.Dtos.LoanDto;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Repositories.*;
import com.mindhub.Homebanking.Services.*;
import com.mindhub.Homebanking.Utils.LoanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.mindhub.Homebanking.Utils.LoanUtils.getPayments;
import static com.mindhub.Homebanking.Utils.LoanUtils.getRoundedAmount;
import static java.util.stream.Collectors.toList;

@Transactional
@RestController
public class LoanController {
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/api/loans")
    public List<LoanDto> getLoans() {
        return loanService.getLoans();
    }
    @PostMapping("/api/loans")
    public ResponseEntity<Object> loans(
            Authentication authentication, @RequestBody LoanApplicationDto loanApplicationDto){

        Loan loan = this.loanService.findById(loanApplicationDto.getLoanId());
        Account account = this.accountService.findByNumber(loanApplicationDto.getDestinationAccountNumber());
        Client client = this.clientService.findByEmail(authentication.getName());


        //Verificar que los campos no esten vacíos o no sean inválidos
        StringBuilder errorMessage = new StringBuilder();
        if (loanApplicationDto.getDestinationAccountNumber().isBlank()) {
            errorMessage.append("El número de cuenta destino es requerido\n");
        }
        if (loanApplicationDto.getAmount() <= 0) {
            errorMessage.append("El monto es requerido y debe ser un número válido\n");
        }
        if (loanApplicationDto.getPayments() <= 0) {
            errorMessage.append("Cuotas es requerido y debe ser un número válido\n");
        }
        if (loanApplicationDto.getLoanId() == 0) {
            errorMessage.append("El tipo de préstamo es requerido\n");
        }


        if (errorMessage.length() > 0) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage.toString());
        }

        //Verificar que el préstamo exista
        if (loan == null) {
            return new ResponseEntity<>("No existe el Préstamo", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cuenta exista
        if (account == null) {
            return new ResponseEntity<>("La cuenta destino no existe", HttpStatus.FORBIDDEN);
        }

        //Verificar que el cliente exista
        if (client == null) {
            return new ResponseEntity<>("El cliente autenticado no existe", HttpStatus.FORBIDDEN);
        }
        //Verificar que el monto solicitado no exceda el monto máximo del préstamo
        if(loanApplicationDto.getAmount() > loan.getMaxAmount()){
            return new ResponseEntity<>("El monto solicitado supera el permitido", HttpStatus.FORBIDDEN);
        }

        //Verificar que la cantidad de cuotas se encuentre entre las disponibles del préstamo
        if (!loan.getPayments().contains(loanApplicationDto.getPayments())){
            return new ResponseEntity<>("Cantidad de cuotas incorrectas", HttpStatus.FORBIDDEN);
        }
        //Verificar que la cuenta de destino exista
        if(loanApplicationDto.getDestinationAccountNumber() == null){
            return new ResponseEntity<>("La cuenta destino no existe", HttpStatus.FORBIDDEN);
        }
        // Verificar que el cliente no tenga ya un préstamo del mismo tipo
        ClientLoan existingLoan = this.clientLoanService.findByLoanAndClient(loan, client);
        if (existingLoan != null) {
            return new ResponseEntity<>("El cliente ya tiene un préstamo del mismo tipo", HttpStatus.FORBIDDEN);
        }


        //Verificar que la cuenta de destino pertenezca al cliente autenticado
        if(!account.getClient().equals(client)){
            return new ResponseEntity<>("La cuenta destino no pertenece al cliente autenticado", HttpStatus.FORBIDDEN);
        }

        ClientLoan clientLoan = new ClientLoan(loanApplicationDto.getAmount(),loanApplicationDto.getAmount()*loan.getInterest(),loanApplicationDto.getPayments(), loanApplicationDto.getPayments());
        clientLoan.setClient(client);
        clientLoan.setLoan(loan);
        clientLoanService.saveClientLoan(clientLoan);

        //Creo la transaccion y la guardo
        Double initialBalanceclientAcc = account.getBalance() + loanApplicationDto.getAmount();
        Transaction creditTransaction = new Transaction(TransactionType.CREDITO, loanApplicationDto.getAmount(),loanApplicationDto.getLoanId()+"crédito aprobado", LocalDateTime.now(),initialBalanceclientAcc, true);
        transactionService.saveTransaction(creditTransaction);

        //Le asigno la transaccion a la cuenta de destino, le agrego el balance y la guardo
        account.addTransaction(creditTransaction);
        account.setBalance(account.getBalance()+creditTransaction.getAmount());
        accountService.saveAccount(account);

        return  new ResponseEntity<>("Create", HttpStatus.CREATED);
    }
    @PostMapping("/api/admin/loan")
    public ResponseEntity<Object> newLoanAdmin(@RequestBody Loan loan) {

        Loan newLoan = new Loan(loan.getName(), loan.getMaxAmount() , loan.getPayments(), loan.getInterest());
        loanService.saveLoan(newLoan);

        return new ResponseEntity<>("Préstamo creado con éxito", HttpStatus.CREATED);
    }

    @Transactional
    @PostMapping("/api/current/loans")
    public ResponseEntity<Object> payLoan(Authentication authentication , @RequestParam long idLoan , @RequestParam String account, @RequestParam double amount) {

        Client client = clientService.findByEmail(authentication.getName());
        Optional<ClientLoan> clientLoan = clientLoanService.findById(idLoan);
        Account accountAuthenticated = accountService.findByNumber(account);
        String description = "Pago de préstamo " + clientLoan.get().getLoan().getName();

        if( clientLoan == null ){
            return new ResponseEntity<>("Este préstamo no existe", HttpStatus.FORBIDDEN);
        } else if( client == null){
            return new ResponseEntity<>("No estás registrado", HttpStatus.FORBIDDEN);}
        else if( clientLoan.get().getPayments() == 0 ){
            return new ResponseEntity<>("Este préstamo ya fue pagado", HttpStatus.FORBIDDEN);
        }
        if (account.isBlank()) {
            return new ResponseEntity<>("Por favor ingresa una cuenta. ", HttpStatus.FORBIDDEN);
        } else {
            if (accountAuthenticated == null) {
                return new ResponseEntity<>("Esta cuenta no existe. ", HttpStatus.FORBIDDEN);
            } else if (!client.getAccounts().contains(accountAuthenticated)) {
                return new ResponseEntity<>("Esta cuenta no te pertenece. ", HttpStatus.FORBIDDEN);
            }
        }
        if (!clientLoan.get().getClient().equals(client)) {
            return new ResponseEntity<>("No solicitaste este préstamo", HttpStatus.FORBIDDEN);
        }

        int payments = LoanUtils.getPayments(clientLoan);
        int roundedAmount = LoanUtils.getRoundedAmount(amount);
        if (roundedAmount != payments) {
            return new ResponseEntity<>("Monto incorrecto de la cuota, debes pagar "+ payments, HttpStatus.FORBIDDEN);
        }

        if ( amount < 1 ){
            return new ResponseEntity<>("Ingresa un valor mayor a 0", HttpStatus.FORBIDDEN);
        }  else if ( accountAuthenticated.getBalance() < amount ){
            return new ResponseEntity<>("Saldo insuficiente en tu cuenta " + accountAuthenticated.getNumber(), HttpStatus.FORBIDDEN);}

        Double initialBalanceaccountAuth = accountAuthenticated.getBalance() - amount;
        accountAuthenticated.setBalance( accountAuthenticated.getBalance() - amount );
        clientLoan.get().setFinalAmount( clientLoan.get().getFinalAmount() - amount);

        Transaction debitLoan = new Transaction(TransactionType.DEBITO, amount, description , LocalDateTime.now(), initialBalanceaccountAuth, true);
        transactionService.saveTransaction(debitLoan);
        accountAuthenticated.addTransaction(debitLoan);

        accountService.saveAccount(accountAuthenticated);

        if ( amount < clientLoan.get().getFinalAmount()){
            clientLoan.get().setPayments(clientLoan.get().getPayments() - 1 ); //para actualizar la cantidad de cuotas
        } else {
            clientLoan.get().setPayments(0);
        }
        return new ResponseEntity<>("Pago efectuado correctamente", HttpStatus.CREATED);
    }



}
