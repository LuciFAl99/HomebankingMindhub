package com.mindhub.Homebanking.Dtos;

import com.mindhub.Homebanking.Models.Card;
import com.mindhub.Homebanking.Models.CardColor;
import com.mindhub.Homebanking.Models.CardType;

import java.time.LocalDate;

public class CardDto {
    private Long id;
    private String cardholder;
    private CardType type;
    private CardColor color;
    private String number;
    private int cvv;
    private LocalDate thruDate;
    private LocalDate fromDate;
    private boolean active;

    private boolean expired;
    public CardDto() {
    }

    public CardDto(Card card) {
        this.id = card.getId();
        this.cardholder = card.getCardholder();
        this.type = card.getType();
        this.color = card.getColor();
        this.number = card.getNumber();
        this.cvv = card.getCvv();
        this.thruDate = card.getThruDate();
        this.fromDate = card.getFromDate();
        this.active = card.isActive();
        this.expired = card.isExpired();
    }

    public Long getId() {
        return id;
    }

    public String getCardholder() {
        return cardholder;
    }

    public CardType getType() {
        return type;
    }

    public CardColor getColor() {
        return color;
    }

    public String getNumber() {
        return number;
    }

    public int getCvv() {
        return cvv;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isExpired() {
        return expired;
    }
}
