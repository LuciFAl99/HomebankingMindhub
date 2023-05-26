package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Models.Account;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.ClientLoan;
import com.mindhub.Homebanking.Models.Loan;

import java.util.Optional;

public interface ClientLoanService {
    ClientLoan findByLoanAndClient(Loan loan, Client client);
    void saveClientLoan(ClientLoan clientLoan);
    Optional<ClientLoan> findById(long id);

}
