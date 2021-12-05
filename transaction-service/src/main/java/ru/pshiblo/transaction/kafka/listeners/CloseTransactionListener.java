package ru.pshiblo.transaction.kafka.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.kafka.KafkaTopics;
import ru.pshiblo.transaction.repository.TransactionRepository;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class CloseTransactionListener {

    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = KafkaTopics.CLOSE)
    @Transactional
    public void closeTransaction(@Payload Transaction transaction) {
        transaction.setStatus(TransactionStatus.CLOSED);
        transactionRepository.save(transaction);
    }
}
