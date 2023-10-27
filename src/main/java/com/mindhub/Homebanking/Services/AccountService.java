package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.AccountDto;
import com.mindhub.Homebanking.Models.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.util.List;

public interface AccountService {
    List<AccountDto> getAccounts();
    void saveAccount(Account account);
    Account findByNumber(String number);
    Account findById (long id);
    ResponseEntity<Object> getAccount(Long id, Authentication authentication);
    ResponseEntity<Object> createAccount(Authentication authentication, String accountType);
    ResponseEntity<Object> deleteAccount (Authentication authentication , long id);
}
