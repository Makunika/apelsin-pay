package ru.pshiblo.personal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.personal.domain.Card;

public interface CardRepository extends JpaRepository<Card, Integer> {
}