package ru.pshiblo.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pshiblo.common.web.ExceptionControllerAdvice;

@Configuration
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class ExceptionControllerAdviceConfig {

    @Bean
    @ConditionalOnMissingBean
    public ExceptionControllerAdvice responseWrapperAdvice(ObjectMapper objectMapper) {
        return new ExceptionControllerAdvice(objectMapper);
    }
}
