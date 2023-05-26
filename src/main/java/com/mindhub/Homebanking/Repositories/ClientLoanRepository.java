package com.mindhub.Homebanking.Repositories;

import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.ClientLoan;
import com.mindhub.Homebanking.Models.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.Optional;

@RepositoryRestResource
public interface ClientLoanRepository extends JpaRepository<ClientLoan, Long> {
    ClientLoan findByLoanAndClient(Loan loan, Client client);
    Optional<ClientLoan> findById(long id);

}
