package ru.pshiblo.transaction.rabbit.listeners.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;

@Service
@RequiredArgsConstructor
public class CheckFromTransactionListener {

    private final RabbitTemplate rabbitTemplate;


    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CHECK_FROM_ROUTE,
                    value = @Queue(RabbitConsts.CHECK_FROM_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void checkTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_FROM_CHECK) {
            throw new TransactionNotAllowedException("status on check not START_FROM_CHECK");
        }

        switch (transaction.getAccountTypeFrom()) {
            case CARD:
                rabbitTemplate.convertAndSend(RabbitConsts.CARD_FROM_CHECK_ROUTE, transaction);
                break;
            case DEPOSIT:
                rabbitTemplate.convertAndSend(RabbitConsts.DEPOSIT_FROM_CHECK_ROUTE, transaction);
                break;
        }
    }
}
