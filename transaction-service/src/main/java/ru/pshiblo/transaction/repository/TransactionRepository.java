package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    boolean existsByStatusAndId(TransactionStatus status, int id);
}