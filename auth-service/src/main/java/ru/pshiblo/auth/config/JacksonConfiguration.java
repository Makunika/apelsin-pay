package ru.pshiblo.auth.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.jackson2.CoreJackson2Module;

import java.lang.reflect.Field;

@Configuration
public class JacksonConfiguration {

    @Bean
    public CoreJackson2Module coreJackson2Module() {
        return new CoreJackson2Module();
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return JsonMapper.builder().configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, false).build();
    }
}
