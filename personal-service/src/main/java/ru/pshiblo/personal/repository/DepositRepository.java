package ru.pshiblo.personal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.personal.domain.Deposit;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {
}