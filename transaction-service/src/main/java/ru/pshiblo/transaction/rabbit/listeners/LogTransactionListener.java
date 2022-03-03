package ru.pshiblo.transaction.rabbit.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.rabbit.RabbitConsts;

@Slf4j
@Service
public class LogTransactionListener {

    @RabbitListener(
            bindings = @QueueBinding(
                    key = { RabbitConsts.CLOSE_ROUTE, RabbitConsts.SEND_ROUTE, RabbitConsts.OPEN_ROUTE, RabbitConsts.OPEN_ROUTE, RabbitConsts.COMMISSION_ROUTE },
                    value = @Queue(RabbitConsts.LOG_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void logTransaction(@Payload Transaction transaction) {
        log.info("Transaction with id {}, status {}",transaction.getId(), transaction.getStatus().name());
    }
}
