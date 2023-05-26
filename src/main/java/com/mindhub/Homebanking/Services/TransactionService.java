package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.Transaction;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionService {
    void saveTransaction(Transaction transaction);
    List<Transaction> findByCreatedBetweenDates(Client client, String string, LocalDateTime date, LocalDateTime date2);
}
