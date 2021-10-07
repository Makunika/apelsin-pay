package ru.pshiblo.kafka.error.resend;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import ru.pshiblo.kafka.error.utils.HeaderUtils;

import java.util.function.BiConsumer;

/**
 * @author Maxim Pshiblo
 */
@Slf4j
@RequiredArgsConstructor
public class ResendKafkaErrorConsumer implements BiConsumer<ConsumerRecord<?, ?>, Exception> {

    private final KafkaTemplate kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void accept(ConsumerRecord<?, ?> record, Exception e) {
        try {
            int maxAttempt = HeaderUtils.readMaxAttempt(record.headers());
            int attempt = HeaderUtils.readAttempt(record.headers());
            String errorTopic = HeaderUtils.readErrorTopic(record.headers());
            log.error("Kafka listener failure. Topic: {}. Key: {}. Value: {}. Attempt: {}. Max attempt: {} ms",
                    record.topic(), record.key(), record.value(), attempt, maxAttempt, e.getCause());
            JsonNode valueObj = objectMapper.readTree(String.valueOf(record.value()));
            ProducerRecord resultRecord;
            if (attempt <= maxAttempt) {
                resultRecord = new ProducerRecord(record.topic(), record.key(), valueObj);
                HeaderUtils.setMaxAttempt(resultRecord.headers(), maxAttempt);
                HeaderUtils.setErrorTopic(resultRecord.headers(), errorTopic);
                HeaderUtils.setAttempt(resultRecord.headers(), attempt + 1);
            } else {
                resultRecord = new ProducerRecord(errorTopic, record.topic() + "_" + record.key(), valueObj);
            }
            kafkaTemplate.send(resultRecord);
        } catch (JsonProcessingException ex) {
            log.error("Error occurred during parsing value", ex);
        }
    }
}
