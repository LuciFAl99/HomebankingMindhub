package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Dtos.AccountDto;
import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Models.Account;
import com.mindhub.Homebanking.Repositories.AccountRepository;
import com.mindhub.Homebanking.Services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class AccountServiceImplement implements AccountService {
    @Autowired
    AccountRepository accountRepository;
    @Override
    public List<AccountDto> getAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDto(account)).collect(toList());
    }

    @Override
    public AccountDto getAccount(Long id) {
        return accountRepository.findById(id)
                .map(account -> new AccountDto(account))
                .orElse(null);
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
}
