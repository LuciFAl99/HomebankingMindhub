
package com.mindhub.Homebanking.Controllers;


import com.mindhub.Homebanking.Dtos.ClientDto;
import com.mindhub.Homebanking.Models.Account;
import com.mindhub.Homebanking.Models.AccountType;
import com.mindhub.Homebanking.Models.Client;
import com.mindhub.Homebanking.Services.AccountService;
import com.mindhub.Homebanking.Services.ClientService;
import com.mindhub.Homebanking.Utils.ClientUtils;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDateTime;
import java.util.List;
@RestController
public class ClientController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;

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
    public ResponseEntity<Object> register(
            @RequestParam String firstName, @RequestParam String lastName,
            @RequestParam String email, @RequestParam String password) {

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

        if (clientService.findByEmail(email) !=  null) {

            return new ResponseEntity<>("El email esta en uso", HttpStatus.FORBIDDEN);

        }
        int randomNumber = ClientUtils.getRandomNumber();
        String accountNumber = ClientUtils.getAccountNumber(randomNumber);

        Client newClient = new Client(firstName, lastName, email, passwordEncoder.encode(password));
        Account newAccount = new Account (accountNumber, LocalDateTime.now(), 0.00, true, AccountType.AHORRO);
        clientService.saveClient(newClient);
        newClient.addAccount(newAccount);
        accountService.saveAccount(newAccount);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}