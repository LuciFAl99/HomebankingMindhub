package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Dtos.PaymentDto;
import com.mindhub.Homebanking.Models.CardColor;
import com.mindhub.Homebanking.Models.CardType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface CardService {
    ResponseEntity<Object> createCard(Authentication authentication, CardType type, CardColor color);
    ResponseEntity<Object> deleteCard (Authentication authentication , Long id);
    ResponseEntity<Object> payWithCard(PaymentDto paymentDTO);
}
