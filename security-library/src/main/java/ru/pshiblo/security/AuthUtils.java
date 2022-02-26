package ru.pshiblo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ru.pshiblo.security.model.AuthUser;

import java.util.Map;
import java.util.Objects;

public class AuthUtils {

    private static final String LOGIN_CLAIM = "user_name";
    private static final String NAME_CLAIM = "name";
    private static final String ID_CLAIM = "id";
    private static final String EMAIL_CLAIM = "email";

    public static AuthUser getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> tokenAttributes = jwtAuthenticationToken.getTokenAttributes();
            AuthUser authUser = new AuthUser(
                    ((String) tokenAttributes.get(LOGIN_CLAIM)),
                    (Long) tokenAttributes.get(ID_CLAIM),
                    ((String) tokenAttributes.get(NAME_CLAIM)),
                    ((String) tokenAttributes.get(EMAIL_CLAIM)),
                    jwtAuthenticationToken.getAuthorities()
            );
            return authUser;
        }
        return null;
    }

    public static long getUserId() {
        return Objects.requireNonNull(getAuthUser()).getId();
    }

}
