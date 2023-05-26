package com.mindhub.Homebanking.Dtos;

import com.mindhub.Homebanking.Models.TransactionType;
import com.mindhub.Homebanking.Models.Transaction;

import java.time.LocalDateTime;

public class TransactionDto {
    private long id;
    private TransactionType type;
    private double amount;
    private String description;
    private LocalDateTime date;
    private double balanceTransaction;
    private boolean active;

    public TransactionDto() {
    }

    public TransactionDto(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType();
        this.amount = transaction.getAmount();
        this.description = transaction.getDescription();
        this.date = transaction.getDate();
        this.balanceTransaction = transaction.getBalanceTransaction();
        this.active = transaction.isActive();
    }


    public long getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public double getBalanceTransaction() {
        return balanceTransaction;
    }

    public boolean isActive() {
        return active;
    }
}


