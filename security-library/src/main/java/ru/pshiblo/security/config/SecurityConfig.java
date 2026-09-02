package ru.pshiblo.security.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import ru.pshiblo.security.auditing.SecurityAuditorAware;
import ru.pshiblo.security.jwt.CustomJwtGrantedAuthoritiesConverter;

import java.util.Collection;

/**
 * Resource-server всех бизнес-сервисов: JWT проверяется по JWK Set auth-service
 * ({@code spring.security.oauth2.resourceserver.jwt.jwk-set-uri} → {@code /oauth2/jwks}),
 * авторитеты — {@code SCOPE_*} из claim {@code scope} плюс роли и {@code ROLE_UNBAN}
 * из {@link CustomJwtGrantedAuthoritiesConverter}. Всё, кроме actuator/public/error, требует ROLE_UNBAN.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {"/actuator", "/actuator/**", "/public/**", "/error/**"};

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests(authz -> authz
                        .antMatchers(PUBLIC_PATHS).permitAll()
                        .anyRequest().access("hasAuthority('ROLE_UNBAN')")
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt()
                        .jwtAuthenticationConverter(jwtAuthenticationConverter()));
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer resourceServerWebSecurityCustomizer() {
        return web -> web.ignoring().antMatchers(PUBLIC_PATHS);
    }

    /** SCOPE_* из claim scope + роли/ROLE_UNBAN из {@link CustomJwtGrantedAuthoritiesConverter}. */
    public static Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        return new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new CustomJwtGrantedAuthoritiesConverter()
        );
    }

    public static JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuditorAware securityAuditorAware() {
        return new SecurityAuditorAware();
    }

}
