package ru.pshiblo.card.rabbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pshiblo.card.rabbit.listeners.ErrorHandler;

@EnableRabbit
@Configuration
public class RabbitConfig {

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public ErrorHandler errorTransactionHandler(RabbitTemplate rabbitTemplate) {
        return new ErrorHandler(rabbitTemplate);
    }
}
