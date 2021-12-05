package ru.pshiblo.transaction.kafka.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.kafka.KafkaTopics;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;
import ru.pshiblo.transaction.service.interfaces.CardService;

import javax.transaction.NotSupportedException;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class SendTransactionListener {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final CardService cardService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = KafkaTopics.SEND)
    @Transactional
    public synchronized void sendTransaction(@Payload Transaction transaction) throws NotSupportedException {
        transaction = transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);

        if (transaction.getStatus() == TransactionStatus.COMMISSION) {
            if (transaction.isInner()) {
                Account toAccount = transaction.isToCard() ?
                        cardService.getByNumber(transaction.getToNumber()).getAccount() :
                        accountService.getByNumber(transaction.getToNumber());

                Account fromAccount = accountService.getByNumber(transaction.getFromNumber());

                Currency transactionCurrency = transaction.getCurrency();

                //TODO: support Currency

                if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
                    if (accountService.getById(fromAccount.getId()).getBalance().compareTo(transaction.getMoney().add(transaction.getCommission())) < 0) {
                        toAccount.setBalance(accountService.getById(toAccount.getId()).getBalance().add(transaction.getMoney()));
                        accountService.save(toAccount);
                        fromAccount.setBalance(accountService.getById(fromAccount.getId()).getBalance().subtract(transaction.getMoney().add(transaction.getCommission())));
                        accountService.save(fromAccount);

                        transaction.setStatus(TransactionStatus.SENT);
                        transaction = transactionRepository.save(transaction);

                        kafkaTemplate.send(KafkaTopics.CLOSE, transaction);
                    } else {
                        throw new TransactionNotAllowedException();
                    }
                }
            } else {
                throw new NotSupportedException("only is inner");
            }
        }
    }
}
