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
public class ApplyPaymentTransactionListener {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.apply_payment",
                    value = @Queue("transaction.apply_payment_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void applyPaymentTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_APPLY_PAYMENT) {
            throw new TransactionNotAllowedException("status on send not END_SEND or not approved");
        }
        transaction.setStatus(TransactionStatus.END_APPLY_PAYMENT);
        repository.save(transaction);
        transaction.setStatus(TransactionStatus.START_TO_CHECK);
        rabbitTemplate.convertAndSend("transaction.check_to", transaction);
    }
}
