package ru.pshiblo.auth.config;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.jackson2.CoreJackson2Module;

@Configuration
public class JacksonConfiguration {

//    @Bean
//    public CoreJackson2Module coreJackson2Module() {
//        return new CoreJackson2Module();
//    }
//
//    @Bean
//    public JavaTimeModule javaTimeModule() {
//        return new JavaTimeModule();
//    }
//
//    @Bean
//    @Primary
//    public ObjectMapper objectMapper() {
//        return JsonMapper.builder()
//                .configure(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS, false)
//                .addModule(javaTimeModule())
//                .addModule(coreJackson2Module())
//                .build();
//    }
}
