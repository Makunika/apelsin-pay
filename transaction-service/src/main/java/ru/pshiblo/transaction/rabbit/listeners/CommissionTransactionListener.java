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

        transaction.setCommissionRate(
                transaction.isInner() ?
                        new BigDecimal("0.05") :
                        new BigDecimal("0.1")
        );

        BigDecimal commissionValue = transaction.getMoney().multiply(transaction.getCommissionRate());
        transaction.setCommissionValue(commissionValue);

        BigDecimal moneyWithCommission = transaction.getMoney().add(commissionValue);
        transaction.setMoneyWithCommission(moneyWithCommission);

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            transaction.setStatus(TransactionStatus.END_COMMISSION);
            transaction = transactionRepository.save(transaction);
            transaction.setStatus(TransactionStatus.START_SEND);
            rabbitTemplate.convertAndSend(RabbitConsts.SEND_ROUTE, transaction);
        }
    }
}
