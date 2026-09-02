package ru.pshiblo.starter.feign;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.security.OAuth2AccessTokenInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Общая Feign-конфигурация всех сервисов.
 * <p>
 * Межсервисные вызовы авторизуются client_credentials-токеном из auth-service.
 * Регистрация клиента берётся из стандартных свойств
 * {@code spring.security.oauth2.client.registration.<id>} / {@code ...provider.<name>.token-uri}
 * (id по умолчанию — {@code internal}, переопределяется {@code feign.oauth2.clientRegistrationId}).
 * Токен кладётся в каждый Feign-запрос через штатный {@link OAuth2AccessTokenInterceptor}
 * из Spring Cloud OpenFeign 3.1.x; свой бин нужен, чтобы не трогать клиентов, которые
 * уже несут собственный заголовок Authorization (например, внешний API Тинькофф).
 */
@Configuration
@EnableFeignClients
@AutoConfigureAfter(OAuth2ClientAutoConfiguration.class)
public class FeignConfiguration {

    public static final String DEFAULT_CLIENT_REGISTRATION_ID = "internal";

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean({ClientRegistrationRepository.class, OAuth2AuthorizedClientService.class})
    public OAuth2AuthorizedClientManager feignOAuth2AuthorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider provider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager manager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientService);
        manager.setAuthorizedClientProvider(provider);
        return manager;
    }

    @Bean
    @ConditionalOnMissingBean(name = "oauth2RequestInterceptor")
    @ConditionalOnBean(OAuth2AuthorizedClientManager.class)
    public RequestInterceptor oauth2RequestInterceptor(
            @Value("${feign.oauth2.clientRegistrationId:" + DEFAULT_CLIENT_REGISTRATION_ID + "}") String clientRegistrationId,
            OAuth2AuthorizedClientManager authorizedClientManager) {
        return new InternalOAuth2RequestInterceptor(
                new OAuth2AccessTokenInterceptor(clientRegistrationId, authorizedClientManager));
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }
}
