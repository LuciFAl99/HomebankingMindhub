package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.LoanApplicationDto;
import com.mindhub.Homebanking.Dtos.LoanDto;
import com.mindhub.Homebanking.Models.Loan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LoanService {
    List<LoanDto> getLoans();
    public Loan findById(long id);
    ResponseEntity<Object> loans(Authentication authentication, LoanApplicationDto loanApplicationDto);
    ResponseEntity<Object> newLoanAdmin(Loan loan);
    ResponseEntity<Object> payLoan(Authentication authentication , long idLoan , String account, double amount);

}
