package ru.pshiblo.transaction.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.stereotype.Service;

/**
 * @author Maxim Pshiblo
 */
@Service
public class KafkaTopics {
    public static final String OPEN = "transaction.open";
    public static final String CLOSE = "transaction.close";
    public static final String CANCEL = "transaction.cancel";
    public static final String COMMISSION = "transaction.commission";
    public static final String SEND = "transaction.check";
    public static final String ERROR = "transaction.error";

    private static NewTopic createDefaultTopic(String name) {
        return TopicBuilder.name(name)
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic newOpenTopic() {
        return createDefaultTopic(OPEN);
    }

    @Bean
    public NewTopic newCloseTopic() {
        return createDefaultTopic(CLOSE);
    }

    @Bean
    public NewTopic newCancelTopic() {
        return createDefaultTopic(CANCEL);
    }

    @Bean
    public NewTopic newCommissionTopic() {
        return createDefaultTopic(COMMISSION);
    }

    @Bean
    public NewTopic newCheckTopic() {
        return createDefaultTopic(SEND);
    }

    public NewTopic newErrorTopic() {
        return createDefaultTopic(ERROR);
    }

}
