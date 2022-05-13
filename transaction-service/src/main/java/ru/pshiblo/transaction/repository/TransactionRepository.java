package ru.pshiblo.transaction.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    boolean existsByStatusAndId(TransactionStatus status, int id);
    List<Transaction> findAllByOwnerUserId(int userId);

    Page<Transaction> findByFromNumberAndOwnerUserIdOrderByCreatedDesc(String fromNumber, Integer ownerUserId, Pageable pageable);

    Page<Transaction> findByFromNumberOrToNumberOrderByCreatedDesc(String fromNumber, String toNumber, Pageable pageable);
}