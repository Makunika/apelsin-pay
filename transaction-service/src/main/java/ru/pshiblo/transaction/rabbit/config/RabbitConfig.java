package ru.pshiblo.transaction.rabbit.config;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pshiblo.transaction.rabbit.listeners.ErrorHandler;

@EnableRabbit
@Configuration
public class RabbitConfig {

    @Bean
    public ErrorHandler errorTransactionHandler(RabbitTemplate rabbitTemplate) {
        return new ErrorHandler(rabbitTemplate);
    }
}
