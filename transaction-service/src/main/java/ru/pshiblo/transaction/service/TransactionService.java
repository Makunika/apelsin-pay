package ru.pshiblo.transaction.service;

import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;

import java.util.List;
import java.util.Optional;

public interface TransactionService {
    Transaction create(Transaction transaction);
    Transaction createSystem(Transaction transaction);

    Transaction createFromTinkoff(Transaction transaction, String redirectUrl);
    void successPayment(int transactionId, long userId);
    Optional<Transaction> getById(int id);
    List<Transaction> getByUserId(int userId);
    Optional<TransactionStatus> getStatusById(int id);
}
