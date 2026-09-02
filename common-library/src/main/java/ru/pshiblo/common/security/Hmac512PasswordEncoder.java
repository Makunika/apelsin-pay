package ru.pshiblo.common.security;

import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Legacy-хеширование паролей: HmacSHA512 с одной статической солью (AUTH_SHA_SALT).
 * Оставлен только для проверки старых хешей; новые пароли кодируются bcrypt
 * (см. {@link ApelsinPasswordEncoders}).
 *
 * @author Maxim Pshiblo
 */
public class Hmac512PasswordEncoder implements PasswordEncoder {

    private static final String ALGORITHM = "HmacSHA512";

    private final byte[] salt;

    public Hmac512PasswordEncoder(String salt) {
        if (salt == null || salt.isEmpty()) {
            throw new IllegalArgumentException("HMAC salt must not be empty");
        }
        this.salt = Utf8.encode(salt);
    }

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            Mac sha512Hmac = Mac.getInstance(ALGORITHM);
            sha512Hmac.init(new SecretKeySpec(salt, ALGORITHM));
            byte[] macData = sha512Hmac.doFinal(Utf8.encode(rawPassword.toString()));
            return Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new IllegalStateException("Error password encode", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        String encodedRawPass = encode(rawPassword);
        return MessageDigest.isEqual(Utf8.encode(encodedRawPass), Utf8.encode(encodedPassword));
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        // всё, что захешировано этим энкодером, должно быть перехешировано в bcrypt
        return true;
    }
}
