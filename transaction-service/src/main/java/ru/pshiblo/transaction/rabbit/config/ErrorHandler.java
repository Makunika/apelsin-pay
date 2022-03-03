package ru.pshiblo.transaction.rabbit.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.RabbitListenerErrorHandler;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.stereotype.Service;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.rabbit.RabbitConsts;

/**
 * @author Maxim Pshiblo
 */
@Slf4j
@RequiredArgsConstructor
public class ErrorHandler implements RabbitListenerErrorHandler {

    private final RabbitTemplate rabbitTemplate;

    @Override
    public Object handleError(Message amqpMessage, org.springframework.messaging.Message<?> message, ListenerExecutionFailedException exception) throws Exception {
        log.info(exception.getMessage());
        if (message != null && message.getPayload() instanceof Transaction) {
            Transaction transaction = (Transaction) message.getPayload();
            transaction.setReasonCancel(exception.getCause().getMessage());
            rabbitTemplate.convertAndSend(RabbitConsts.CANCEL_ROUTE, transaction);
        }

        return null;
    }
}