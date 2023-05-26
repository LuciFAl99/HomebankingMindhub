package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Dtos.LoanDto;
import com.mindhub.Homebanking.Models.ClientLoan;
import com.mindhub.Homebanking.Models.Loan;
import com.mindhub.Homebanking.Repositories.LoanRepository;
import com.mindhub.Homebanking.Services.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class LoanServiceImplement implements LoanService {
    @Autowired
    LoanRepository loanRepository;

    @Override
    public List<LoanDto> getLoans() {
        return loanRepository.findAll().stream().map(loan -> new LoanDto(loan)).collect(toList());
    }

    @Override
    public Loan findById(long id) {
        return loanRepository.findById(id);
    }

    @Override
    public void saveLoan(Loan loan) {
        loanRepository.save(loan);
    }

}
