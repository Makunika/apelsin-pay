package ru.pshiblo.auth.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import ru.pshiblo.auth.model.AuthUser;

import java.util.ArrayList;

/**
 * Claims access-токена, которые читают {@code security-library} (AuthUtils,
 * CustomJwtGrantedAuthoritiesConverter) и фронтенды.
 * <p>
 * Для всех токенов: {@code client_id}. Для пользовательских (authorization_code):
 * {@code user_name}, {@code id}, {@code email}, {@code name}, {@code status}, {@code lock},
 * {@code authorities}. Claim {@code scope} SAS пишет сам — как JSON-массив.
 */
public class ApelsinTokenCustomizer implements OAuth2TokenCustomizer<JwtEncodingContext> {

    public static final String CLAIM_CLIENT_ID = "client_id";
    public static final String CLAIM_USER_NAME = "user_name";
    public static final String CLAIM_ID = "id";
    public static final String CLAIM_EMAIL = "email";
    public static final String CLAIM_NAME = "name";
    public static final String CLAIM_STATUS = "status";
    public static final String CLAIM_LOCK = "lock";
    public static final String CLAIM_AUTHORITIES = "authorities";

    @Override
    public void customize(JwtEncodingContext context) {
        if (!OAuth2TokenType.ACCESS_TOKEN.equals(context.getTokenType())) {
            return;
        }
        context.getClaims().claim(CLAIM_CLIENT_ID, context.getRegisteredClient().getClientId());

        Authentication principal = context.getPrincipal();
        if (principal != null && principal.getPrincipal() instanceof AuthUser) {
            AuthUser user = (AuthUser) principal.getPrincipal();
            context.getClaims()
                    .claim(CLAIM_USER_NAME, user.getLogin())
                    .claim(CLAIM_ID, user.getId())
                    .claim(CLAIM_EMAIL, user.getEmail())
                    .claim(CLAIM_NAME, user.getName())
                    .claim(CLAIM_STATUS, user.getConfirmedStatus())
                    .claim(CLAIM_LOCK, user.getIsLock())
                    .claim(CLAIM_AUTHORITIES, new ArrayList<>(user.getRoles()));
        }
    }
}
