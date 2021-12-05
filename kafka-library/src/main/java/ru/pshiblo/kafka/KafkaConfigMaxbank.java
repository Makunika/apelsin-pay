package ru.pshiblo.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonSerializer;
import ru.pshiblo.kafka.error.DefaultKafkaErrorHandler;
import ru.pshiblo.kafka.error.resend.ResendKafkaErrorHandler;
import ru.pshiblo.kafka.error.resend.ResendKafkaProducerInterceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maxim Pshiblo
 */
@Slf4j
@EnableKafka
@Configuration
@AutoConfigureOrder(-67)
public class KafkaConfigMaxbank {

    @Value("${kafka.bootstrap}")
    private String kafkaBootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaGroupId;

    @Bean
    public String kafkaBootstrapServers() {
        log.info("Using kafka brokers: {}", kafkaBootstrapServers);
        return kafkaBootstrapServers;
    }

    @Bean
    @Primary
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers());
        final KafkaAdmin admin = new KafkaAdmin(configs);
        admin.setFatalIfBrokerNotAvailable(true);
        return admin;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory(String kafkaBootstrapServers, ObjectMapper objectMapper) throws UnknownHostException {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, InetAddress.getLocalHost().getHostName());
        configProps.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, String.valueOf(30000));
        configProps.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, ResendKafkaProducerInterceptor.class.getName());
        return new DefaultKafkaProducerFactory<>(configProps, new StringSerializer(),
                new JsonSerializer<>(objectMapper));
    }

    @Primary
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        KafkaTemplate<String, Object> ret = new KafkaTemplate<>(producerFactory);
        ret.setAllowNonTransactional(true);
        return ret;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory, ObjectMapper objectMapper,
            KafkaTemplate<String, Object> kafkaTemplate) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setMessageConverter(new JsonMessageConverter(objectMapper));
        factory.setConcurrency(4);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        ResendKafkaErrorHandler resendErrorHandler = new ResendKafkaErrorHandler(kafkaTemplate, objectMapper);
        factory.setErrorHandler(new DefaultKafkaErrorHandler(resendErrorHandler));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(String kafkaBootstrapServers) {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        configProps.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
        configProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configProps.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        configProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        configProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(configProps);
    }
}
