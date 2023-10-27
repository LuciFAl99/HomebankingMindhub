package com.mindhub.Homebanking.Controllers;

import com.mindhub.Homebanking.Dtos.AccountDto;
import com.mindhub.Homebanking.Services.AccountService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/api/accounts")
    public List<AccountDto> getAccounts() {
        return accountService.getAccounts();
    }

    @GetMapping("/api/accounts/{id}")
    public ResponseEntity<Object> getAccount(@PathVariable Long id, Authentication authentication) {
        return accountService.getAccount(id, authentication);
    }

    @PostMapping("/api/clients/current/accounts")
    public ResponseEntity<Object> createAccount(Authentication authentication, @RequestParam String accountType) {
       return accountService.createAccount(authentication, accountType);

    }

    @PutMapping("/api/clients/current/accounts")
    public ResponseEntity<Object> deleteAccount (Authentication authentication , @RequestParam long id){
        return accountService.deleteAccount(authentication, id);
    }
}
