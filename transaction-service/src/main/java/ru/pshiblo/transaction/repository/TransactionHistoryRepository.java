package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.transaction.domain.TransactionHistory;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {
}