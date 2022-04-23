package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.domain.TransactionHistory;
import ru.pshiblo.transaction.mappers.TransactionMapper;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogTransactionListener {

    private final TransactionHistoryRepository repository;
    private final TransactionMapper mapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = {
                        RabbitConsts.CLOSE_ROUTE,
                        RabbitConsts.OPEN_ROUTE,
                        RabbitConsts.SEND_ROUTE,
                        RabbitConsts.CANCEL_ROUTE,
                        RabbitConsts.CHECK_FROM_ROUTE,
                        RabbitConsts.CHECK_TO_ROUTE,
                        RabbitConsts.COMMISSION_ROUTE,
                        RabbitConsts.ERROR_ROUTE,
                        RabbitConsts.APPLY_PAYMENTS_ROUTE,
                        RabbitConsts.ADD_PAYMENT_ROUTE,
                        RabbitConsts.DEPOSIT_AFTER_SEND_ROUTE,
                        RabbitConsts.CARD_AFTER_SEND_ROUTE,
                        RabbitConsts.DEPOSIT_FROM_CHECK_ROUTE,
                        RabbitConsts.CARD_FROM_CHECK_ROUTE,
                        RabbitConsts.DEPOSIT_TO_CHECK_ROUTE,
                        RabbitConsts.CARD_TO_CHECK_ROUTE,
                        RabbitConsts.OPEN_SYSTEM_ROUTE,
                        RabbitConsts.SEND_SYSTEM_ROUTE
                    },
                    value = @Queue(RabbitConsts.LOG_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void logTransaction(@Payload Transaction transaction, Message amqpMessage) {
        log.info("Transaction with id {}, status {}",transaction.getId(), transaction.getStatus().name());
        if (transaction.getType() == null) {
            log.info(transaction.toString());
            return;
        }
        String receivedRoutingKey = amqpMessage.getMessageProperties().getReceivedRoutingKey();
        TransactionHistory history = mapper.toHistory(transaction, receivedRoutingKey);
        repository.save(history);
    }
}
