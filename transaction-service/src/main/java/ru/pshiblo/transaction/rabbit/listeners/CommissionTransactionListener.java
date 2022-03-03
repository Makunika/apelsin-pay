package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
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
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.COMMISSION_ROUTE,
                    value = @Queue(RabbitConsts.COMMISSION_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
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
                rabbitTemplate.convertAndSend(RabbitConsts.SEND_ROUTE, transaction);
            }
        }
    }
}
