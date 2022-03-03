package ru.pshiblo.transaction.service.interfaces;

import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction create(Transaction transaction);
    Optional<Transaction> getById(int id);
    List<Transaction> getByUserId(int userId);
    Optional<TransactionStatus> getStatusById(int id);
}
