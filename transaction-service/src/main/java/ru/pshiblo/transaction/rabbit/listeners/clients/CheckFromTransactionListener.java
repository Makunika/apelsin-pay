package ru.pshiblo.transaction.rabbit.listeners.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
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

import javax.validation.Valid;

@Service
@RequiredArgsConstructor
public class CheckFromTransactionListener {

    private final RabbitTemplate rabbitTemplate;


    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.check_from",
                    value = @Queue("transaction.check_from_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void checkTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_FROM_CHECK) {
            throw new TransactionNotAllowedException("status on check not START_FROM_CHECK");
        }

        switch (transaction.getAccountTypeFrom()) {
            case BUSINESS:
                rabbitTemplate.convertAndSend("transaction.check_from.business", transaction);
                break;
            case PERSONAL:
                rabbitTemplate.convertAndSend("transaction.check_from.personal", transaction);
                break;
        }
    }
}
