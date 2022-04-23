package ru.pshiblo.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.InternalException;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.model.PayoutModel;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public Transaction create(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);
        transaction.setType(TransactionType.TRANSFER);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend(RabbitConsts.OPEN_ROUTE, savedTransaction);
        return savedTransaction;
    }

    @Override
    public Transaction createSystem(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);
        transaction.setType(TransactionType.TRANSFER);

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
