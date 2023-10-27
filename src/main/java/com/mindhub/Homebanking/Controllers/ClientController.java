package com.mindhub.Homebanking.Controllers;

import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Services.ClientService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
public class ClientController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/api/clients")
    public List<ClientDto> getClients() {
        return clientService.getClients();
    }

    @GetMapping("/api/clients/{id}")
    public ClientDto getClient(@PathVariable Long id) {
        return clientService.getClient(id);
    }

    @GetMapping("api/clients/current")
    public ClientDto getCurrentClient(Authentication authentication) {
        return clientService.getCurrentClient(authentication);
    }

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping( "/api/clients")
    public ResponseEntity<Object> register(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email, @RequestParam String password) {
        return clientService.register(firstName, lastName, email, password);
    }

}