package com.mindhub.Homebanking.Controllers;

import com.mindhub.Homebanking.Dtos.PaymentDto;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Repositories.CardRepository;
import com.mindhub.Homebanking.Repositories.ClientRepository;
import com.mindhub.Homebanking.Services.AccountService;
import com.mindhub.Homebanking.Services.CardService;
import com.mindhub.Homebanking.Services.ClientService;
import com.mindhub.Homebanking.Services.TransactionService;
import com.mindhub.Homebanking.Utils.CardUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
@CrossOrigin(origins = {"*"})
@RestController
public class CardController {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;

    @PostMapping("/api/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType type, @RequestParam CardColor color) {


        Client client = clientService.findByEmail(authentication.getName());
        List<Card> accountsActive = client.getCards().stream().filter(card -> card.isActive()).collect(toList());
        Set<Card> cards = client.getCards().stream().filter(card -> card.getType() == type).collect(Collectors.toSet());

      /*  if (client.getCards().stream().filter(e -> e.getType().toString().equals(type.toString())).count() >= 3) {
            return new ResponseEntity<>("403 Ya tiene 3 tarjetas de ese tipo", HttpStatus.FORBIDDEN);
        }*/

    /*    if (cards.stream().anyMatch(card -> card.getColor() == color)) {
            return new ResponseEntity<>("Ya tienes una tarjeta de ese tipo", HttpStatus.FORBIDDEN);
        }*/
        boolean hasMatchingCard = client.getCards().stream()
                .anyMatch(card -> card.getType().equals(CardType.valueOf(type.toString()))
                        && card.getColor().equals(CardColor.valueOf(color.toString()))
                        && card.isActive());

        if (hasMatchingCard) {
            return new ResponseEntity<>("Tú ya tienes una tarjeta de " + type + " " + color, HttpStatus.FORBIDDEN);
        }
        String cardNumber = CardUtils.getCardNumber();
        int cvv = CardUtils.getCVV();

        LocalDate fromDate = LocalDate.now();
        LocalDate thruDate = fromDate.plusYears(5);
        boolean active = true;
        boolean expired = (thruDate.isBefore(LocalDate.now()) || !active);
        if ( cards.size() <= 20){
            Card cardGenerated = new Card(client.getFirstName() + " " + client.getLastName(), type, color, cardNumber, cvv, thruDate, fromDate, active, expired);        cardService.saveCard(cardGenerated);
            cardService.saveCard(cardGenerated);
            client.addCard(cardGenerated);
            clientService.saveClient(client);
        }

        return new ResponseEntity<>("Tarjeta creada con éxito", HttpStatus.CREATED);

    }

    @PutMapping("/api/clients/current/cards")
    public ResponseEntity<Object> deleteCard (
            Authentication authentication , @RequestParam Long id){

        Client client = clientService.findByEmail(authentication.getName());
        Card card = cardService.getById(id);

        if(card == null){
            return new ResponseEntity<>("Esta tarjeta no existe", HttpStatus.FORBIDDEN);
        } else if ( !card.isActive() ){
            return new ResponseEntity<>("Esta tarjeta está inactiva", HttpStatus.FORBIDDEN);}
        if (client == null) {
            return new ResponseEntity<>("No eres un cliente registrado", HttpStatus.FORBIDDEN);
        }else if( client.getCards().stream().filter(card1 -> card1.getId() == id).collect(toList()).size() == 0 ){
            return new ResponseEntity<>("Esta tarjeta no te pertenece", HttpStatus.FORBIDDEN);}
        card.setActive(false);
        cardService.saveCard(card);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PostMapping("/api/clients/current/pay-card")
    public ResponseEntity<Object> payWithCard(@RequestBody PaymentDto paymentDTO) {
        StringBuilder errorBuilder = new StringBuilder();

        if (paymentDTO.getNumber().isBlank()) {
            errorBuilder.append("Número de tarjeta requerido\n");
        } else {
            if (!cardService.existsByCvv(paymentDTO.getCvv())) {
                errorBuilder.append("CVV inválido\n");
            }
            if (!cardService.existsByNumber(paymentDTO.getNumber())) {
                errorBuilder.append("La tarjeta no existe\n");
            }
            if (paymentDTO.getTypeCard() == null) {
                errorBuilder.append("Debes seleccionar un tipo de tarjeta (DEBITO o CREDITO)\n");
            }
            if (paymentDTO.getTypeCard() != null && !errorBuilder.toString().contains("Debes seleccionar un tipo de tarjeta")) {
                Card cardUsed = cardService.findByNumber(paymentDTO.getNumber());
                if (cardUsed.getType() != paymentDTO.getTypeCard()) {
                    errorBuilder.append("El tipo de tarjeta seleccionado no coincide con el número de tarjeta\n");
                }
            }
        }

        if (paymentDTO.getAmount() <= 0) {
            errorBuilder.append("Debes ingresar un monto válido\n");
        }
        if (paymentDTO.getDescription().isBlank()) {
            errorBuilder.append("Descripción es requerido\n");
        }
        if (paymentDTO.getEmail().isBlank()) {
            errorBuilder.append("Email es requerido\n");
        }

        Client client = null;
        if (!errorBuilder.toString().contains("Email es requerido")) {
            client = clientService.findByEmail(paymentDTO.getEmail());
            if (client == null) {
                errorBuilder.append("El email ingresado no existe\n");
            }
        }

        if (errorBuilder.length() > 0) {
            return new ResponseEntity<>(errorBuilder.toString().trim(), HttpStatus.FORBIDDEN);
        }

        Card cardUsed = cardService.findByNumber(paymentDTO.getNumber());

        boolean hasCard = client.getCards()
                .stream()
                .anyMatch(card -> card.getId() == cardUsed.getId());

        if (!hasCard) {
            return new ResponseEntity<>("El email ingresado pertenece a otra cuenta", HttpStatus.FORBIDDEN);
        }

        if (cardUsed.getThruDate().isBefore(LocalDate.of(2023, 5, 16))) {
            return new ResponseEntity<>("La tarjeta expiró", HttpStatus.FORBIDDEN);
        }

        Optional<Account> optionalAccountToBeDebited = client.getAccounts()
                .stream()
                .filter(acc -> acc.getBalance() >= paymentDTO.getAmount())
                .findFirst();

        if (optionalAccountToBeDebited.isPresent()) {
            Account accountToBeDebited = optionalAccountToBeDebited.get();
            // Realizo las operaciones con la cuenta encontrada

            Double initialBalanceaccountToBeDebited = accountToBeDebited.getBalance() - paymentDTO.getAmount();
            Transaction paymentCard = new Transaction(TransactionType.DEBITO, paymentDTO.getAmount(), paymentDTO.getDescription(), LocalDateTime.now(), initialBalanceaccountToBeDebited, true);
            transactionService.saveTransaction(paymentCard);
            accountToBeDebited.addTransaction(paymentCard);
            double newBalanceDebit = accountToBeDebited.getBalance() - paymentDTO.getAmount(); // Calcula el nuevo saldo
            accountToBeDebited.setBalance(newBalanceDebit); // Actualizo el saldo
            accountService.saveAccount(accountToBeDebited); // Guardo la cuenta

            return new ResponseEntity<>("Pago exitoso", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Fondos insuficientes", HttpStatus.FORBIDDEN);
        }
    }


}