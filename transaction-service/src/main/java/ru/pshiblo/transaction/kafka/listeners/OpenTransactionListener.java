package ru.pshiblo.transaction.kafka.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.kafka.KafkaTopics;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;
import ru.pshiblo.transaction.service.interfaces.CardService;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenTransactionListener {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CardService cardService;


    @KafkaListener(topics = KafkaTopics.OPEN)
    @Transactional
    public void openTransaction(@Payload Transaction transaction) {
        log.info("trans open!");
        log.info(transaction.toString());
        transaction.setStatus(TransactionStatus.OPENED);

        Account account = accountService.getByNumber(transaction.getFromNumber());

        if (account.getLock()) {
            throw new TransactionNotAllowedException("Account is lock");
        }

        if (account.getBalance().compareTo(transaction.getMoney()) > 0) {
            throw new TransactionNotAllowedException("Balance small");
        }

        transaction = transactionRepository.save(transaction);

        //if may be ban - on approve!!!!

        transaction.setStatus(TransactionStatus.APPROVED);

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            //TODO: save to history
            transactionRepository.save(transaction);
            kafkaTemplate.send(KafkaTopics.COMMISSION, transaction);
        }
    }
}
