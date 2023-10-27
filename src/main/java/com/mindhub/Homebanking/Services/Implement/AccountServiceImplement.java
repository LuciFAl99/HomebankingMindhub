package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Dtos.AccountDto;
import com.mindhub.Homebanking.Models.Account;
import com.mindhub.Homebanking.Models.AccountType;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Repositories.AccountRepository;
import com.mindhub.Homebanking.Repositories.ClientRepository;
import com.mindhub.Homebanking.Services.AccountService;
import com.mindhub.Homebanking.Utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class AccountServiceImplement implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientRepository clientRepository;
    @Override
    public List<AccountDto> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDto(account)).collect(toList());
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public Account findByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public Account findById(long id) {
        return accountRepository.findById(id);
    }

    @Override
    public ResponseEntity<Object> getAccount(Long id, Authentication authentication) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = findById(id);

        if (account != null && account.getClient().equals(client)) {
            return ResponseEntity.ok(new AccountDto(account));
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Esta cuenta no te pertenece");
    }

    @Override
    public ResponseEntity<Object> createAccount(Authentication authentication, String accountType) {
        int randomNumber = AccountUtils.getRandomNumber();
        String accountNumber = AccountUtils.getAccountNumber(randomNumber);

        Client client = clientRepository.findByEmail(authentication.getName());
        List<Account> accountsActive = client.getAccounts().stream().filter(account -> account.isActive()).collect(Collectors.toList());
        Set<Account> accounts = client.getAccounts();

        if (accountsActive.size() <= 2 && accounts.size() <= 20 ) {
            Account accountGenerated = new Account(accountNumber, LocalDateTime.now(), 0.00, true, AccountType.valueOf(accountType.toUpperCase()));
            accountRepository.save(accountGenerated);
            client.addAccount(accountGenerated);
            clientRepository.save(client);
        }else{
            return new ResponseEntity<>("Alcanzaste el límite de cuentas creadas", HttpStatus.FORBIDDEN);
        }
        if ( !accountType.equalsIgnoreCase("CORRIENTE") && !accountType.equalsIgnoreCase("AHORRO")){
            return new ResponseEntity<>("Selecciona un tipo de cuenta correcto", HttpStatus.FORBIDDEN);}

        return new ResponseEntity<>( "Cuenta creada con éxito",HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> deleteAccount(Authentication authentication, long id) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Account account = accountRepository.findById(id);

        if( account == null ){
            return new ResponseEntity<>("Esta cuenta no existe", HttpStatus.FORBIDDEN);
        } else if( account.getBalance() > 0 ){
            return new ResponseEntity<>("No puedes eliminar esta cuenta porque tiene dinero", HttpStatus.FORBIDDEN);
        }

        if (client == null) {
            return new ResponseEntity<>("No eres un cliente", HttpStatus.FORBIDDEN);
        }else if( client.getAccounts().stream().filter(account1 -> account1.getId() == id).collect(toList()).size() == 0 ){
            return new ResponseEntity<>("Esta cuenta no te pertenece", HttpStatus.FORBIDDEN);}

        account.setActive(false);
        account.getTransactions().stream().forEach( transaction -> transaction.setActive(false));
        accountRepository.save(account);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
