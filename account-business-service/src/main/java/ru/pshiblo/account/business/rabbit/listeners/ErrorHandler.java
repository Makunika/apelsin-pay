package ru.pshiblo.account.business.rabbit.listeners;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import ru.pshiblo.account.business.model.Transaction;

/**
 * @author Maxim Pshiblo
 */
@Slf4j
@RequiredArgsConstructor
public class ErrorHandler implements RabbitListenerErrorHandler {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
        log.info(exception.getCause().getMessage());
        if (message != null && message.getPayload() instanceof Transaction) {
            Transaction transaction = (Transaction) message.getPayload();
            rabbitTemplate.convertAndSend("transaction.cancel", new TransactionError(
                    exception.getCause().getMessage(),
                    transaction.getId()
            ));
        }

        return null;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TransactionError {
        private String reason;
        private Integer transactionId;
    }
}
