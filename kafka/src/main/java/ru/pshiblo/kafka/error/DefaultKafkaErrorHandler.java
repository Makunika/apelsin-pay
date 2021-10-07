package ru.pshiblo.kafka.error;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import ru.pshiblo.kafka.error.resend.ResendKafkaErrorHandler;
import ru.pshiblo.kafka.error.utils.HeaderUtils;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public class DefaultKafkaErrorHandler extends SeekToCurrentErrorHandler {

    private final ResendKafkaErrorHandler resendKafkaErrorHandler;

    public DefaultKafkaErrorHandler(ResendKafkaErrorHandler resendKafkaErrorHandler) {
        this.resendKafkaErrorHandler = resendKafkaErrorHandler;
    }

    @Override
    public void handle(Exception exception, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
        if (records != null && !records.isEmpty() && HeaderUtils.isAllowResend(records.get(0).headers())) {
            resendKafkaErrorHandler.handle(exception, records, consumer, container);
        } else {
            super.handle(exception, records, consumer, container);
        }
    }
}
