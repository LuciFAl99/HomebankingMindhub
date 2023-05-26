package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Models.ClientLoan;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<ClientDto> getClients();
    ClientDto getClient(Long id);
    void saveClient(Client client);

    ClientDto getCurrentClient(Authentication authentication);

    Client findByEmail(String email);

    boolean existsByEmail(String email);

}
