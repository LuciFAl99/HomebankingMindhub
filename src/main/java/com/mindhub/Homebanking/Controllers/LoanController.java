package com.mindhub.Homebanking.Controllers;

import com.mindhub.Homebanking.Dtos.LoanApplicationDto;
import com.mindhub.Homebanking.Dtos.LoanDto;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
@RestController
public class LoanController {
    @Autowired
    private LoanService loanService;

    @GetMapping("/api/loans")
    public List<LoanDto> getLoans() {
        return loanService.getLoans();
    }
    @PostMapping("/api/loans")
    public ResponseEntity<Object> loans(Authentication authentication, @RequestBody LoanApplicationDto loanApplicationDto){
        return loanService.loans(authentication, loanApplicationDto);
    }
    @PostMapping("/api/admin/loan")
    public ResponseEntity<Object> newLoanAdmin(@RequestBody Loan loan) {
        return loanService.newLoanAdmin(loan);
    }

    @Transactional
    @PostMapping("/api/current/loans")
    public ResponseEntity<Object> payLoan(Authentication authentication , @RequestParam long idLoan , @RequestParam String account, @RequestParam double amount) {
        return loanService.payLoan(authentication, idLoan, account, amount);
    }



}
