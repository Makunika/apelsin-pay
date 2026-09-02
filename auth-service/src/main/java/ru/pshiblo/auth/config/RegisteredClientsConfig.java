package ru.pshiblo.auth.config;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Реестр OAuth2-клиентов (in-memory).
 * <ul>
 *   <li>{@code browser_main} / {@code browser_payment} — public-клиенты SPA:
 *       authorization_code + PKCE (S256), без секрета, без экрана consent.
 *       Redirect URI и TTL access-токена — из {@code auth.clients.*} (env URL_LOGIN_MAIN / URL_LOGIN_PAYMENT).</li>
 *   <li>Сервисные клиенты — client_credentials, секрет из env {@code <NAME>_SERVICE_PASSWORD},
 *       в памяти хранится только bcrypt-хеш секрета.</li>
 * </ul>
 * Scope'ы сервисов должны совпадать с {@code spring.security.oauth2.client.registration.internal.scope}
 * соответствующего сервиса в config-service.
 */
@Configuration
@RequiredArgsConstructor
public class RegisteredClientsConfig {

    public static final String BROWSER_MAIN_CLIENT_ID = "browser_main";
    public static final String BROWSER_PAYMENT_CLIENT_ID = "browser_payment";
    public static final String SCOPE_USER = "user";
    public static final String SCOPE_USER_PAYMENT = "user_payment";
    public static final String SCOPE_SERVER = "server";

    private static final List<ServiceClient> SERVICE_CLIENTS = List.of(
            new ServiceClient("info-personal-service", "INFO_PERSONAL_SERVICE_PASSWORD", SCOPE_SERVER, "users_s"),
            new ServiceClient("info-business-service", "INFO_BUSINESS_SERVICE_PASSWORD", SCOPE_SERVER),
            new ServiceClient("users-service", "USERS_SERVICE_PASSWORD", SCOPE_SERVER),
            new ServiceClient("auth-service", "AUTH_SERVICE_PASSWORD", SCOPE_SERVER, "info_b_s", "info_p_s"),
            new ServiceClient("transaction-service", "TRANSACTION_SERVICE_PASSWORD", SCOPE_SERVER, "account_b_s", "account_p_s"),
            new ServiceClient("account-business-service", "ACCOUNT_BUSINESS_SERVICE_PASSWORD", SCOPE_SERVER, "info_b_s"),
            new ServiceClient("payment-service", "PAYMENT_SERVICE_PASSWORD", SCOPE_SERVER, "account_b_s", "info_b_s", "transaction_s"),
            new ServiceClient("account-personal-service", "ACCOUNT_PERSONAL_SERVICE_PASSWORD", SCOPE_SERVER, "transaction_s")
    );

    private final AuthProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final Environment env;

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        List<RegisteredClient> clients = new ArrayList<>();
        clients.add(browserClient(BROWSER_MAIN_CLIENT_ID, SCOPE_USER, properties.getClient(AuthProperties.BROWSER_MAIN)));
        clients.add(browserClient(BROWSER_PAYMENT_CLIENT_ID, SCOPE_USER_PAYMENT, properties.getClient(AuthProperties.BROWSER_PAYMENT)));
        for (ServiceClient serviceClient : SERVICE_CLIENTS) {
            clients.add(serviceClient(serviceClient));
        }
        return new InMemoryRegisteredClientRepository(clients);
    }

    private RegisteredClient browserClient(String clientId, String scope, AuthProperties.BrowserClient settings) {
        List<String> redirectUris = new ArrayList<>();
        for (String uri : settings.getRedirectUris()) {
            if (StringUtils.hasText(uri)) {
                redirectUris.add(uri.trim());
            }
        }
        if (redirectUris.isEmpty()) {
            throw new IllegalStateException("No redirect URIs configured for client " + clientId);
        }
        return RegisteredClient.withId(clientId)
                .clientId(clientId)
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUris(uris -> uris.addAll(redirectUris))
                .scope(scope)
                .clientSettings(ClientSettings.builder()
                        .requireProofKey(true)
                        .requireAuthorizationConsent(false)
                        .build())
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(settings.getAccessTokenTtl())
                        .build())
                .build();
    }

    private RegisteredClient serviceClient(ServiceClient serviceClient) {
        String secret = env.getProperty(serviceClient.getSecretEnv());
        if (!StringUtils.hasText(secret)) {
            throw new IllegalStateException("Environment variable " + serviceClient.getSecretEnv()
                    + " with secret of client " + serviceClient.getClientId() + " is not set");
        }
        return RegisteredClient.withId(serviceClient.getClientId())
                .clientId(serviceClient.getClientId())
                .clientSecret(passwordEncoder.encode(secret))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .scopes(scopes -> scopes.addAll(serviceClient.getScopes()))
                .build();
    }

    @Value
    private static class ServiceClient {
        String clientId;
        String secretEnv;
        List<String> scopes;

        ServiceClient(String clientId, String secretEnv, String... scopes) {
            this.clientId = clientId;
            this.secretEnv = secretEnv;
            this.scopes = List.of(scopes);
        }
    }
}
