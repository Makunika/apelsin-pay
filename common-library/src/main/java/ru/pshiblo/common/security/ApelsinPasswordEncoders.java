package ru.pshiblo.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Единый {@link PasswordEncoder} проекта.
 * <p>
 * Новые хеши — {@code {bcrypt}...}. Старые Hmac-хеши хранятся без префикса и
 * проверяются через {@link Hmac512PasswordEncoder} (default encoder for matches);
 * для них {@link PasswordEncoder#upgradeEncoding} возвращает {@code true}, и
 * Spring Security перехеширует пароль при первом успешном входе.
 */
public final class ApelsinPasswordEncoders {

    public static final String BCRYPT_ID = "bcrypt";
    public static final String HMAC512_ID = "hmac512";
    public static final String BCRYPT_PREFIX = "{" + BCRYPT_ID + "}";

    private ApelsinPasswordEncoders() {
    }

    public static PasswordEncoder createDelegatingPasswordEncoder(String hmacSalt) {
        Hmac512PasswordEncoder legacy = new Hmac512PasswordEncoder(hmacSalt);
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(BCRYPT_ID, new BCryptPasswordEncoder());
        encoders.put(HMAC512_ID, legacy);
        DelegatingPasswordEncoder delegating = new DelegatingPasswordEncoder(BCRYPT_ID, encoders);
        delegating.setDefaultPasswordEncoderForMatches(legacy);
        return delegating;
    }
}
