package com.mindhub.Homebanking.Dtos;

import com.mindhub.Homebanking.Models.Loan;

import java.util.ArrayList;
import java.util.List;

public class LoanDto {
    private long id;
    private String name;
    private double maxAmount;
    private List<Integer> payments;
    private double interest;

    public LoanDto() {
    }

    public LoanDto(Loan loan) {
        this.id = loan.getId();
        this.name = loan.getName();
        this.maxAmount = loan.getMaxAmount();
        this.payments = loan.getPayments();
        this.interest = loan.getInterest();

    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getMaxAmount() {
        return maxAmount;
    }
    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }
    public List<Integer> getPayments() {
        return payments;
    }
    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }
    public double getInterest() {
        return interest;
    }
}
