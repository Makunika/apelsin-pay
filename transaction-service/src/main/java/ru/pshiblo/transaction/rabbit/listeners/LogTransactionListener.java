package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.domain.TransactionHistory;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogTransactionListener {

    private final TransactionHistoryRepository repository;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = { RabbitConsts.CLOSE_ROUTE, RabbitConsts.SEND_ROUTE, RabbitConsts.OPEN_ROUTE, RabbitConsts.CANCEL_ROUTE, RabbitConsts.COMMISSION_ROUTE },
                    value = @Queue(RabbitConsts.LOG_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void logTransaction(@Payload Transaction transaction) {
        log.info("Transaction with id {}, status {}",transaction.getId(), transaction.getStatus().name());

        TransactionHistory history = TransactionHistory.fromTransaction(transaction);
        repository.save(history);
    }
}
