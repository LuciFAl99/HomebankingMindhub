package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Dtos.PaymentDto;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Repositories.AccountRepository;
import com.mindhub.Homebanking.Repositories.CardRepository;
import com.mindhub.Homebanking.Repositories.ClientRepository;
import com.mindhub.Homebanking.Repositories.TransactionRepository;
import com.mindhub.Homebanking.Services.CardService;
import com.mindhub.Homebanking.Utils.CardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class CardServiceImplement implements CardService {
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ClientRepository clientRepository;
    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    AccountRepository accountRepository;


    @Override
    public ResponseEntity<Object> createCard(Authentication authentication, CardType type, CardColor color) {
        Client client = clientRepository.findByEmail(authentication.getName());
        List<Card> accountsActive = client.getCards().stream().filter(card -> card.isActive()).collect(toList());
        Set<Card> cards = client.getCards().stream().filter(card -> card.getType() == type).collect(Collectors.toSet());

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
            Card cardGenerated = new Card(client.getFirstName() + " " + client.getLastName(), type, color, cardNumber, cvv, thruDate, fromDate, active, expired);
            cardRepository.save(cardGenerated);
            cardRepository.save(cardGenerated);
            client.addCard(cardGenerated);
            clientRepository.save(client);
        }

        return new ResponseEntity<>("Tarjeta creada con éxito", HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> deleteCard(Authentication authentication, Long id) {
        Client client = clientRepository.findByEmail(authentication.getName());
        Card card = cardRepository.getById(id);

        if(card == null){
            return new ResponseEntity<>("Esta tarjeta no existe", HttpStatus.FORBIDDEN);
        } else if ( !card.isActive() ){
            return new ResponseEntity<>("Esta tarjeta está inactiva", HttpStatus.FORBIDDEN);}
        if (client == null) {
            return new ResponseEntity<>("No eres un cliente registrado", HttpStatus.FORBIDDEN);
        }else if( client.getCards().stream().filter(card1 -> card1.getId() == id).collect(toList()).size() == 0 ){
            return new ResponseEntity<>("Esta tarjeta no te pertenece", HttpStatus.FORBIDDEN);}
        card.setActive(false);
        cardRepository.save(card);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<Object> payWithCard(PaymentDto paymentDTO) {
        StringBuilder errorBuilder = new StringBuilder();

        if (paymentDTO.getNumber().isBlank()) {
            errorBuilder.append("Número de tarjeta requerido\n");
        } else {
            if (!cardRepository.existsByCvv(paymentDTO.getCvv())) {
                errorBuilder.append("CVV inválido\n");
            }
            if (!cardRepository.existsByNumber(paymentDTO.getNumber())) {
                errorBuilder.append("La tarjeta no existe\n");
            }
            if (paymentDTO.getTypeCard() == null) {
                errorBuilder.append("Debes seleccionar un tipo de tarjeta (DEBITO o CREDITO)\n");
            }
            if (paymentDTO.getTypeCard() != null && !errorBuilder.toString().contains("Debes seleccionar un tipo de tarjeta")) {
                Card cardUsed = cardRepository.findByNumber(paymentDTO.getNumber());
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
            client = clientRepository.findByEmail(paymentDTO.getEmail());
            if (client == null) {
                errorBuilder.append("El email ingresado no existe\n");
            }
        }

        if (errorBuilder.length() > 0) {
            return new ResponseEntity<>(errorBuilder.toString().trim(), HttpStatus.FORBIDDEN);
        }

        Card cardUsed = cardRepository.findByNumber(paymentDTO.getNumber());

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
            transactionRepository.save(paymentCard);
            accountToBeDebited.addTransaction(paymentCard);
            double newBalanceDebit = accountToBeDebited.getBalance() - paymentDTO.getAmount(); // Calcula el nuevo saldo
            accountToBeDebited.setBalance(newBalanceDebit); // Actualizo el saldo
            accountRepository.save(accountToBeDebited); // Guardo la cuenta

            return new ResponseEntity<>("Pago exitoso", HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>("Fondos insuficientes", HttpStatus.FORBIDDEN);
        }
    }

}
