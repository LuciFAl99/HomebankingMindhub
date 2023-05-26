package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.ClientLoan;
import com.mindhub.Homebanking.Models.Loan;
import com.mindhub.Homebanking.Repositories.ClientLoanRepository;
import com.mindhub.Homebanking.Services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientLoanServiceImplement implements ClientLoanService {
    @Autowired
    ClientLoanRepository clientLoanRepository;

    @Override
    public ClientLoan findByLoanAndClient(Loan loan, Client client) {
        return clientLoanRepository.findByLoanAndClient(loan, client);
    }

    @Override
    public void saveClientLoan(ClientLoan clientLoan) {
       clientLoanRepository.save(clientLoan);
    }

    @Override
    public Optional<ClientLoan> findById(long id) {
        return clientLoanRepository.findById(id);
    }

}
