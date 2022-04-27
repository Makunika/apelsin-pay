package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public Transaction create(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend("transaction.open", savedTransaction);
        return savedTransaction;
    }

    @Override
    public Transaction createSystem(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);
        transaction.setType(TransactionType.TRANSFER);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend("transaction.open.system", savedTransaction);
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
