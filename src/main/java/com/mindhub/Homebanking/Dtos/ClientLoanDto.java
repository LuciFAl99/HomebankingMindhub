package com.mindhub.Homebanking.Dtos;

import com.mindhub.Homebanking.Models.ClientLoan;
import com.mindhub.Homebanking.Models.Loan;

public class ClientLoanDto {
    private long id;
    private long loanId;
    private String name;
    private double amount;
    private double finalAmount;
    private int payments;
   private double interest;
   private int originalPayments;


    public ClientLoanDto() {
    }

    public ClientLoanDto(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
        this.finalAmount = clientLoan.getFinalAmount();
        this.payments = clientLoan.getPayments();
        this.interest = clientLoan.getLoan().getInterest();
        this.originalPayments = clientLoan.getOriginalPayments();
    }

    public long getId() {
        return id;
    }
    public long getLoanId() {
        return loanId;
    }
    public String getName() {
        return name;
    }
    public double getAmount() {
        return amount;
    }
    public double getFinalAmount() {
        return finalAmount;
    }
    public double getInterest() {
        return interest;
    }
    public int getPayments() {
        return payments;
    }

    public int getOriginalPayments() {
        return originalPayments;
    }
}
