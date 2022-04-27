package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
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
import ru.pshiblo.transaction.repository.TransactionHistoryRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogTransactionListener {

    private final TransactionHistoryRepository repository;
    private final TransactionMapper mapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.#",
                    value = @Queue("log_t_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
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
