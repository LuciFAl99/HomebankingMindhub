package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.AccountDto;
import com.mindhub.Homebanking.Models.Account;

import java.util.List;

public interface AccountService {
    List<AccountDto> getAccounts();
    AccountDto getAccount(Long id);
    void saveAccount(Account account);
    Account findByNumber(String number);
    Account findById (long id);
}
