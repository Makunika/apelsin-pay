package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class CloseTransactionListener {

    private final TransactionRepository transactionRepository;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CLOSE_ROUTE,
                    value = @Queue(RabbitConsts.CLOSE_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void closeTransaction(@Payload Transaction transaction) {
        transaction.setStatus(TransactionStatus.CLOSED);
        transactionRepository.save(transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CANCEL_ROUTE,
                    value = @Queue(RabbitConsts.CANCEL_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void cancelTransaction(@Payload Transaction transaction) {
        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);
    }
}
