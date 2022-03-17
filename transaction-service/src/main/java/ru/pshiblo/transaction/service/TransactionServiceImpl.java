package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Card;
import ru.pshiblo.account.service.CardService;
import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Transaction create(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend(RabbitConsts.OPEN_ROUTE, savedTransaction);
        return savedTransaction;
    }

    @Override
    public Transaction createSystem(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend(RabbitConsts.OPEN_SYSTEM_ROUTE, savedTransaction);
        return savedTransaction;
    }

    @Override
    public Optional<Transaction> getById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<Transaction> getByUserId(int userId) {
        return repository.findAllByOwnerUserId(userId);
    }

    @Override
    public Optional<TransactionStatus> getStatusById(int id) {
        return getById(id).map(Transaction::getStatus);
    }
}
