package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.transaction.domain.Card;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Integer> {
    List<Card> findByUserId(int userId);
    Optional<Card> findByNumber(String number);
    boolean existsByNumber(String number);
}