package com.mindhub.Homebanking.Controllers;

import com.mindhub.Homebanking.Dtos.PaymentDto;
import com.mindhub.Homebanking.Models.*;
import com.mindhub.Homebanking.Services.CardService;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = {"*"})
@RestController
public class CardController {
    @Autowired
    private CardService cardService;

    @PostMapping("/api/clients/current/cards")
    public ResponseEntity<Object> createCard(Authentication authentication, @RequestParam CardType type, @RequestParam CardColor color) {
        return cardService.createCard(authentication, type, color);
    }

    @PutMapping("/api/clients/current/cards")
    public ResponseEntity<Object> deleteCard (Authentication authentication , @RequestParam Long id){
        return cardService.deleteCard(authentication, id);
    }
    @PostMapping("/api/clients/current/pay-card")
    public ResponseEntity<Object> payWithCard(@RequestBody PaymentDto paymentDTO) {
        return cardService.payWithCard(paymentDTO);
    }


}