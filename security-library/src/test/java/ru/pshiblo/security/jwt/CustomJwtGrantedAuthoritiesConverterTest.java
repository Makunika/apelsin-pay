package ru.pshiblo.security.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJwtGrantedAuthoritiesConverterTest {

    private final CustomJwtGrantedAuthoritiesConverter converter = new CustomJwtGrantedAuthoritiesConverter();

    @Test
    void userToken_lockFalse_grantsRolesAndUnban() {
        Jwt jwt = jwt(Map.of(
                "authorities", List.of("ROLE_USER", "ROLE_MODERATOR"),
                "lock", false,
                "scope", List.of("user")));

        assertThat(names(converter.convert(jwt)))
                .containsExactlyInAnyOrder("ROLE_USER", "ROLE_MODERATOR", "ROLE_UNBAN");
    }

    @Test
    void userToken_lockTrue_hasNoUnban() {
        Jwt jwt = jwt(Map.of("authorities", List.of("ROLE_USER"), "lock", true, "scope", List.of("user")));

        assertThat(names(converter.convert(jwt))).containsExactly("ROLE_USER");
    }

    @Test
    void serverToken_scopeServerAsArray_grantsUnban() {
        // Spring Authorization Server пишет claim scope как JSON-массив
        Jwt jwt = jwt(Map.of("client_id", "transaction-service", "scope", List.of("server", "account_b_s")));

        assertThat(names(converter.convert(jwt))).containsExactly("ROLE_UNBAN");
    }

    @Test
    void serverToken_withoutServerScope_hasNoUnban() {
        Jwt jwt = jwt(Map.of("client_id", "x", "scope", List.of("account_b_s")));

        assertThat(converter.convert(jwt)).isEmpty();
    }

    @Test
    void delegatingConverter_asUsedBySecurityConfig_addsScopeAuthoritiesForIsUser() {
        DelegatingJwtGrantedAuthoritiesConverter delegating = new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(), converter);
        Jwt jwt = jwt(Map.of("authorities", List.of("ROLE_USER"), "lock", false, "scope", List.of("user")));

        assertThat(names(delegating.convert(jwt)))
                .containsExactlyInAnyOrder("SCOPE_user", "ROLE_USER", "ROLE_UNBAN");
    }

    private static Jwt jwt(Map<String, Object> claims) {
        return new Jwt("token", Instant.now(), Instant.now().plusSeconds(60), Map.of("alg", "RS256"), claims);
    }

    private static List<String> names(Collection<GrantedAuthority> authorities) {
        return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
    }
}
