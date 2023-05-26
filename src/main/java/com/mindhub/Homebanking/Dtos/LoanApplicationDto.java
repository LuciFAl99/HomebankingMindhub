package com.mindhub.Homebanking.Dtos;

public class LoanApplicationDto {
    private long  loanId;
    private double amount;
    private int payments;
    private String destinationAccountNumber;

    public LoanApplicationDto() {
    }

    public LoanApplicationDto(long loanId, double amount, int payments, String destinationAccountNumber) {
        this.loanId = loanId;
        this.amount = amount;
        this.payments = payments;
        this.destinationAccountNumber = destinationAccountNumber;

    }

    public long getLoanId() {
        return loanId;
    }

    public void setLoanId(long loanId) {
        this.loanId = loanId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getPayments() {
        return payments;
    }

    public void setPayments(int payments) {
        this.payments = payments;
    }

    public String getDestinationAccountNumber() {
        return destinationAccountNumber;
    }

    public void setDestinationAccountNumber(String destinationAccountNumber) {
        this.destinationAccountNumber = destinationAccountNumber;
    }

}
