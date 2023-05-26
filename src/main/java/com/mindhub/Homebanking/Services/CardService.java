package com.mindhub.Homebanking.Services;

import com.mindhub.Homebanking.Models.Card;

public interface CardService {
    void saveCard(Card card);
    Card getById(Long id);
    Card findByNumber(String number);
    boolean existsByNumber(String number);
    boolean existsByCvv(int cvv);
}
