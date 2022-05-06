package ru.pshiblo.transaction.clients.config;

import feign.RequestInterceptor;
import org.springframework.cloud.openfeign.security.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

public class InternalClientConfig {

    @Bean
    public RequestInterceptor oauth2RequestInterceptor(ClientCredentialsResourceDetails resourceDetails) {
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails);
    }

    @Bean
    public OAuth2RestTemplate oAuth2RestTemplate(ClientCredentialsResourceDetails resourceDetails) {
        return new OAuth2RestTemplate(resourceDetails);
    }
}
