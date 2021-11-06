package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.transaction.domain.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {
}