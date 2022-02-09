package ru.pshiblo.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.jackson2.CoreJackson2Module;

@Configuration
public class JacksonConfiguration {

    @Bean
    public CoreJackson2Module coreJackson2Module() {
        return new CoreJackson2Module();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(coreJackson2Module());
        return mapper;
    }
}
