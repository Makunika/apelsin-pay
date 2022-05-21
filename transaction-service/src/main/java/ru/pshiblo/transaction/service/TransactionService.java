package ru.pshiblo.transaction.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.model.InfoPrepare;

import java.math.BigDecimal;
import java.util.Optional;

public interface TransactionService {
    Transaction create(Transaction transaction);
    Transaction createSystem(Transaction transaction);
    Transaction createFromTinkoff(Transaction transaction, String redirectUrl);
    void successPayment(int transactionId, long userId);
    Optional<Transaction> getById(int id);
    Page<Transaction> getByUserIdAndNumber(int userId, String number, Pageable pageable);
    Optional<TransactionStatus> getStatusById(int id);
    InfoPrepare getPrepareInfo(String toNumber, String fromNumber, BigDecimal money, Currency currency, long userId);
}
