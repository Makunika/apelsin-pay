package ru.pshiblo.transaction.rabbit.listeners.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.pshiblo.transaction.repository.TransactionRepository;

import javax.validation.Valid;

@Service
@Slf4j
@RequiredArgsConstructor
public class CheckToTransactionListener {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.check_to",
                    value = @Queue("transaction.check_to_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void applyPaymentTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_TO_CHECK) {
            throw new TransactionNotAllowedException("status on send not START_TO_CHECK");
        }

        switch (transaction.getAccountTypeTo()) {
            case BUSINESS:
                rabbitTemplate.convertAndSend("transaction.check_to.business", transaction);
                break;
            case PERSONAL:
                rabbitTemplate.convertAndSend("transaction.check_to.personal", transaction);
                break;
        }
    }
}
