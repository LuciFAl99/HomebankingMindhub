package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Models.Account;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.Transaction;
import com.mindhub.Homebanking.Repositories.AccountRepository;
import com.mindhub.Homebanking.Repositories.TransactionRepository;
import com.mindhub.Homebanking.Services.AccountService;
import com.mindhub.Homebanking.Services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImplement implements TransactionService {
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountService accountService;
    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

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
}
