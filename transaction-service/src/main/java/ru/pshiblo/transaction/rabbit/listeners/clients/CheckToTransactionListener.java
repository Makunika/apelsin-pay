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
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
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
                    key = RabbitConsts.CHECK_TO_ROUTE,
                    value = @Queue(RabbitConsts.CHECK_TO_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void applyPaymentTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_TO_CHECK) {
            throw new TransactionNotAllowedException("status on send not START_TO_CHECK");
        }

        switch (transaction.getAccountTypeTo()) {
            case BUSINESS:
                rabbitTemplate.convertAndSend(RabbitConsts.CARD_TO_CHECK_ROUTE, transaction);
                break;
            case PERSONAL:
                rabbitTemplate.convertAndSend(RabbitConsts.DEPOSIT_TO_CHECK_ROUTE, transaction);
                break;
        }
    }
}
