package ru.pshiblo.transaction.kafka.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.kafka.KafkaTopics;
import ru.pshiblo.transaction.repository.AccountRepository;
import ru.pshiblo.transaction.repository.CardRepository;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class CommissionTransactionListener {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final CardRepository cardRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.COMMISSION)
    @Transactional
    public void commissionTransaction(@Payload Transaction transaction) {
        transaction = transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);

        if (transaction.getStatus() == TransactionStatus.APPROVED) {

            transaction.setCommission(
                    transaction.isInner() ?
                            new BigDecimal(0) :
                            transaction.getMoney().multiply(new BigDecimal("0.05"))
            );

            transaction.setStatus(TransactionStatus.COMMISSION);
            if (transactionRepository.existsByStatusAndId(TransactionStatus.APPROVED, transaction.getId())) {
                transaction = transactionRepository.save(transaction);
                kafkaTemplate.send(KafkaTopics.SEND, transaction);
            }
        }
    }
}
