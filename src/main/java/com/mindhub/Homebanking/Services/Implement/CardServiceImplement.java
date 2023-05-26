package com.mindhub.Homebanking.Services.Implement;

import com.mindhub.Homebanking.Models.Card;
import com.mindhub.Homebanking.Repositories.CardRepository;
import com.mindhub.Homebanking.Services.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CardServiceImplement implements CardService {
    @Autowired
    CardRepository cardRepository;
    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    @Override
    public Card getById(Long id) {
        return cardRepository.getById(id);
    }

    @Override
    public Card findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public boolean existsByNumber(String number) {
        return cardRepository.existsByNumber(number);
    }

    @Override
    public boolean existsByCvv(int cvv) {
        return cardRepository.existsByCvv(cvv);
    }

}
