package com.mindhub.Homebanking.Repositories;

import com.mindhub.Homebanking.Models.Card;
import com.mindhub.Homebanking.Models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface CardRepository extends JpaRepository <Card, Long>{
    Card getById(Long id);
    Card findByNumber(String number);
    boolean existsByNumber (String number);
    boolean existsByCvv(int cvv);
}
