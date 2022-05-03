package ru.pshiblo.transaction.tinkoff.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TinkoffApiConfig {

    @Bean
    public RequestInterceptor feignRequestInterceptor(){
        return  requestTemplate -> requestTemplate.header(
                "Authorization",
                "Bearer TinkoffOpenApiSandboxSecretToken"
        );
    }
}
