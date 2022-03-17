package ru.pshiblo.transaction.rabbit.listeners.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.account.repository.AccountRepository;
import ru.pshiblo.account.repository.CardRepository;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CommissionTransactionListener {

    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.COMMISSION_ROUTE,
                    value = @Queue(RabbitConsts.COMMISSION_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void commissionTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_COMMISSION) {
            throw new TransactionNotAllowedException("status on commision not START_COMMISION");
        }

        log.info("START COMMISION {}", transaction.getId());
        log.info(transaction.toString());
        transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);
        log.info(transaction.toString());
        transaction.setCommissionRate(
                transaction.isInner() ?
                        new BigDecimal("2") :
                        new BigDecimal("0.1")
        );

        log.info(transaction.toString());
        BigDecimal commissionValue = transaction.getMoney().multiply(transaction.getCommissionRate());
        transaction.setCommissionValue(commissionValue);

        BigDecimal moneyWithCommission = transaction.getMoney().add(commissionValue);
        transaction.setMoneyWithCommission(moneyWithCommission);

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            transaction.setStatus(TransactionStatus.END_COMMISSION);
            log.info(transaction.toString());
            transaction = transactionRepository.save(transaction);
            log.info(transaction.toString());
            transaction.setStatus(TransactionStatus.START_FROM_CHECK);
            log.info(transaction.toString());
            log.info("FINISH COMMISION {}", transaction.getId());
            rabbitTemplate.convertAndSend(RabbitConsts.CHECK_FROM_ROUTE, transaction);
        }
    }
}
