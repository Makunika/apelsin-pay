package ru.pshiblo.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Цепочка фильтров для всего, что не является OAuth2-эндпоинтом: встроенная React-страница логина
 * ({@code /index.html} + {@code POST /perform_login}), её статика, actuator.
 * Аутентификация пользователей — {@link ru.pshiblo.auth.service.impl.UserServiceImpl} +
 * {@link org.springframework.security.crypto.password.PasswordEncoder} из common-library
 * (bcrypt с прозрачным перехешированием legacy Hmac-хешей).
 *
 * @author Maxim Pshiblo
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String LOGIN_PROCESSING_URL = "/perform_login";

    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/**", "/error", LOGIN_PROCESSING_URL).permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/index*", "/static/**", "/*.js", "/*.json", "/*.ico", "/favicon/**").permitAll()
                        .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage(AuthorizationServerConfig.LOGIN_PAGE)
                        .loginProcessingUrl(LOGIN_PROCESSING_URL))
                .httpBasic(basic -> basic.disable());
        return http.build();
    }
}
