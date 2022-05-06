package ru.pshiblo.transaction.tinkoff.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class TinkoffApiConfig {

    @Bean
    public RequestInterceptor tinkoffRequestInterceptor() {
        return requestTemplate -> requestTemplate.header(
                "Authorization",
                "Bearer TinkoffOpenApiSandboxSecretToken"
        );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
