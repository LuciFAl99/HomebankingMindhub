package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Repositories.ClientRepository;
import com.mindhub.Homebanking.Services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ClientServiceImplement implements ClientService {
    @Autowired
    ClientRepository clientRepository;

    @Override
    public List<ClientDto> getClients() {
        return clientRepository.findAll().stream().map(client -> new ClientDto(client)).collect(toList());
    }

    @Override
    public ClientDto getClient(Long id) {
        return clientRepository.findById(id)
                .map(client -> new ClientDto(client))
                .orElse(null);
    }

    @Override
    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    @Override
    public ClientDto getCurrentClient(Authentication authentication) {
        return new ClientDto(clientRepository.findByEmail(authentication.getName()));
    }

    @Override
    public Client findByEmail(String email) {
        return clientRepository.findByEmail(email) ;
    }

    @Override
    public boolean existsByEmail(String email) {
        return clientRepository.existsByEmail(email);
    }

}
