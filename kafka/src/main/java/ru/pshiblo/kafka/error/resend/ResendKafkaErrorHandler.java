package ru.pshiblo.kafka.error.resend;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;

import static org.springframework.util.backoff.BackOffExecution.STOP;

/**
 * @author Maxim Pshiblo
 */
public class ResendKafkaErrorHandler extends SeekToCurrentErrorHandler {

    public ResendKafkaErrorHandler(KafkaTemplate kafkaTemplate, ObjectMapper objectMapper) {
        super(new ResendKafkaErrorConsumer(kafkaTemplate, objectMapper), () -> () -> STOP);
    }
}
