package ru.pshiblo.starter.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;

/**
 * Добавляет client_credentials bearer-токен во внутренние Feign-вызовы.
 * Если у запроса уже есть заголовок Authorization (клиент к внешнему API
 * с собственной авторизацией), ничего не делает.
 */
public class InternalOAuth2RequestInterceptor implements RequestInterceptor {

    private final RequestInterceptor delegate;

    public InternalOAuth2RequestInterceptor(RequestInterceptor delegate) {
        this.delegate = delegate;
    }

    @Override
    public void apply(RequestTemplate template) {
        boolean hasAuthorization = template.headers().keySet().stream()
                .anyMatch(HttpHeaders.AUTHORIZATION::equalsIgnoreCase);
        if (hasAuthorization) {
            return;
        }
        delegate.apply(template);
    }
}
