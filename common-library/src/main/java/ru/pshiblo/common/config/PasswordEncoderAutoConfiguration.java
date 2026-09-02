package ru.pshiblo.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.pshiblo.common.security.ApelsinPasswordEncoders;

/**
 * Поднимает общий {@link PasswordEncoder} в сервисах, которым нужна проверка/выдача
 * паролей (auth-service, users-service): условие — задана соль legacy-энкодера
 * {@code encoder.sha512.salt} (переменная AUTH_SHA_SALT).
 */
@Configuration
@ConditionalOnClass(PasswordEncoder.class)
@ConditionalOnProperty("encoder.sha512.salt")
public class PasswordEncoderAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder(@Value("${encoder.sha512.salt}") String hmacSalt) {
        return ApelsinPasswordEncoders.createDelegatingPasswordEncoder(hmacSalt);
    }
}
