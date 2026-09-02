package ru.pshiblo.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Настройки авторизационного сервера, зависящие от окружения (см. shared/auth-service.yml).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {

    public static final String BROWSER_MAIN = "browser-main";
    public static final String BROWSER_PAYMENT = "browser-payment";

    /** Issuer токенов (claim iss, metadata endpoint). Если пуст — берётся из запроса. */
    private String issuer;

    private Keys keys = new Keys();

    private Cors cors = new Cors();

    /** Браузерные (public, PKCE) клиенты: ключи {@code browser-main} и {@code browser-payment}. */
    private Map<String, BrowserClient> clients = new LinkedHashMap<>();

    public BrowserClient getClient(String name) {
        BrowserClient client = clients.get(name);
        if (client == null) {
            throw new IllegalStateException("auth.clients." + name + " is not configured");
        }
        return client;
    }

    @Getter
    @Setter
    public static class Keys {
        /** Каталог с RSA-парой для подписи JWT (AUTH_KEYS_PATH, по умолчанию ./.keys). */
        private String path = "./.keys";
    }

    @Getter
    @Setter
    public static class Cors {
        /** Origin'ы SPA, которым разрешено ходить в /oauth2/token напрямую. */
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Getter
    @Setter
    public static class BrowserClient {
        private List<String> redirectUris = new ArrayList<>();
        private Duration accessTokenTtl = Duration.ofHours(1);
    }
}
