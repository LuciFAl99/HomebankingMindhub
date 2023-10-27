package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Models.Account;
import com.mindhub.Homebanking.Models.AccountType;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Repositories.AccountRepository;
import com.mindhub.Homebanking.Repositories.ClientRepository;
import com.mindhub.Homebanking.Services.ClientService;
import com.mindhub.Homebanking.Utils.ClientUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ClientServiceImplement implements ClientService {
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

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
    public ClientDto getCurrentClient(Authentication authentication) {
        return new ClientDto(clientRepository.findByEmail(authentication.getName()));
    }

    @Override
    public ResponseEntity<Object> register(String firstName, String lastName, String email, String password) {

        StringBuilder errors = new StringBuilder();

        if (firstName.isBlank()) {
            errors.append("Nombre es requerido\n");
        }

        if (lastName.isBlank()) {
            errors.append("Apellido es requerido\n");
        }

        if (email.isBlank()) {
            errors.append("Email es requerido\n");
        }

        if (password.isBlank()) {
            errors.append("La contraseña es requerida\n");
        } else if (password.length() < 8) {
            errors.append("La contraseña debe tener al menos 8 caracteres\n");
        }

        if (errors.length() > 0) {
            return new ResponseEntity<>(errors.toString(), HttpStatus.FORBIDDEN);
        }

        if (clientRepository.findByEmail(email) !=  null) {

            return new ResponseEntity<>("El email esta en uso", HttpStatus.FORBIDDEN);

        }
        int randomNumber = ClientUtils.getRandomNumber();
        String accountNumber = ClientUtils.getAccountNumber(randomNumber);

        Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        Account newAccount = new Account (accountNumber, LocalDateTime.now(), 0.00, true, AccountType.AHORRO);
        clientRepository.save(newClient);
        newClient.addAccount(newAccount);
        accountRepository.save(newAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
