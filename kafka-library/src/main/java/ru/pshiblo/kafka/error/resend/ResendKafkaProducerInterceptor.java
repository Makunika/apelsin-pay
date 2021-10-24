package ru.pshiblo.kafka.error.resend;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import ru.pshiblo.kafka.error.utils.HeaderUtils;
import ru.pshiblo.kafka.error.annotation.EnableErrorTopic;

import java.util.Map;

/**
 * @author Maxim Pshiblo
 */
public class ResendKafkaProducerInterceptor implements ProducerInterceptor<String, Object> {
    @Override
    public ProducerRecord<String, Object> onSend(ProducerRecord<String, Object> record) {
        EnableErrorTopic enableErrorTopic = record.value().getClass().getDeclaredAnnotation(EnableErrorTopic.class);
        if (enableErrorTopic != null) {
            String errorTopic = enableErrorTopic.errorTopic();
            int maxAttempt = enableErrorTopic.maxAttempt();
            HeaderUtils.setErrorTopic(record.headers(), errorTopic);
            HeaderUtils.setMaxAttempt(record.headers(), maxAttempt);
            HeaderUtils.setAllowResend(record.headers());
        }
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata recordMetadata, Exception e) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
