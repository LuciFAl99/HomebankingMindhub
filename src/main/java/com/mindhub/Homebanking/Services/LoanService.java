package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.LoanDto;
import com.mindhub.Homebanking.Models.Loan;

import java.util.List;

public interface LoanService {
    List<LoanDto> getLoans();
    public Loan findById(long id);
    void saveLoan (Loan loan);

}
