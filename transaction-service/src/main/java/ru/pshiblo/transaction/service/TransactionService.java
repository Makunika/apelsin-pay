package ru.pshiblo.transaction.service;

import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction create(Transaction transaction);
    Transaction createSystem(Transaction transaction);
    Optional<Transaction> getById(int id);
    List<Transaction> getByUserId(int userId);
    Optional<TransactionStatus> getStatusById(int id);
}
