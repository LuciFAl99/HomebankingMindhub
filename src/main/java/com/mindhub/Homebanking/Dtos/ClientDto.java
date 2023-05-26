package com.mindhub.Homebanking.Dtos;

import com.mindhub.Homebanking.Models.Client;

import java.util.List;
import java.util.stream.Collectors;

public class ClientDto {

    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<AccountDto> accounts;
    private List<ClientLoanDto> loans;
    private List<CardDto> cards;

    public ClientDto(Client client) {
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts()
                .stream()
                .map(account -> new AccountDto(account))
                .collect(Collectors.toList());
        this.loans = client.getClientLoans()
                .stream()
                .map(loan -> new ClientLoanDto(loan))
                .collect(Collectors.toList());
        this.cards = client.getCards()
                .stream()
                .map(card -> new CardDto(card))
                .collect(Collectors.toList());
    }

    public long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public List<AccountDto> getAccounts() {
        return accounts;
    }

    public List<ClientLoanDto> getLoans() {
        return loans;
    }
    public List<CardDto> getCards() {
        return cards;
    }

    @Override
    public String toString() {
        return "ClientDto{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                '}';
    }

}
