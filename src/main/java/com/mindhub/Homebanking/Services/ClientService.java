package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {
    List<ClientDto> getClients();
    ClientDto getClient(Long id);

    ClientDto getCurrentClient(Authentication authentication);

    ResponseEntity<Object> register(String firstName,  String lastName, String email,String password);

}
