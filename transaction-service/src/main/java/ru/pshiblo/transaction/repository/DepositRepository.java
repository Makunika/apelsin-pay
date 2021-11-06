package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.transaction.domain.Deposit;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {
}