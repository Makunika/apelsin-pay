package ru.pshiblo.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import ru.pshiblo.security.enums.ConfirmedStatus;
import ru.pshiblo.security.model.AuthUser;

import java.util.*;
import java.util.stream.Collectors;

public class AuthUtils {

    private static final String LOGIN_CLAIM = "user_name";
    private static final String CLIENT_CLAIM = "client_id";
    private static final String NAME_CLAIM = "name";
    private static final String STATUS_CLAIM = "status";
    private static final String COMPANIES_CLAIM = "companies";
    private static final String ID_CLAIM = "id";
    private static final String EMAIL_CLAIM = "email";

    public static final long SERVER_ID = -1111L;
    public static final long UNAUTH_ID = -1000L;

    public static String ROLE_ADMINISTRATOR = "ROLE_ADMINISTRATOR";
    public static String ROLE_USER = "ROLE_USER";
    public static String ROLE_MODERATOR = "ROLE_MODERATOR";

    public static AuthUser getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken) {
            JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
            Map<String, Object> tokenAttributes = jwtAuthenticationToken.getTokenAttributes();
            if (jwtAuthenticationToken.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("SCOPE_server"))) {
                return new AuthUser(
                        ((String) tokenAttributes.get(CLIENT_CLAIM)),
                        SERVER_ID,
                        ((String) tokenAttributes.get(CLIENT_CLAIM)),
                        "none",
                        true,
                        null,
                        new ArrayList<>(),
                        jwtAuthenticationToken.getAuthorities()
                );
            } else {
                return new AuthUser(
                        ((String) tokenAttributes.get(LOGIN_CLAIM)),
                        (Long) tokenAttributes.get(ID_CLAIM),
                        ((String) tokenAttributes.get(NAME_CLAIM)),
                        ((String) tokenAttributes.get(EMAIL_CLAIM)),
                        false,
                        ConfirmedStatus.valueOf(((String) tokenAttributes.getOrDefault(STATUS_CLAIM, ConfirmedStatus.NOT_CONFIRMED.name()))),
                        new ArrayList<>(((Collection<String>) tokenAttributes.getOrDefault(COMPANIES_CLAIM, new ArrayList<>()))),
                        jwtAuthenticationToken.getAuthorities()
                );
            }
        }
        return new AuthUser(
                "UNAUTH",
                UNAUTH_ID,
                "UNAUTH",
                "UNAUTH",
                false,
                null,
                new ArrayList<>(),
                new ArrayList<>()
        );
    }

    public static long getUserId() {
        return Objects.requireNonNull(getAuthUser()).getId();
    }

    public static boolean hasRole(String role) {
        return getAuthUser().getAuthoritiesSimple().stream().anyMatch(r -> r.equals(role));
    }
}
