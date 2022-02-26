package ru.pshiblo.account.encoder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.codec.Utf8;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author Maxim Pshiblo
 */
@Component
@Slf4j
public class Hmac512PasswordEncoder implements PasswordEncoder {

    @Value("${encoder.sha512.salt}")
    private String salt;

    @Override
    public String encode(CharSequence rawPassword) {
        String result = null;

        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            final byte[] byteKey = Utf8.encode(salt);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, "HmacSHA512");
            sha512Hmac.init(keySpec);
            byte[] macData = sha512Hmac.doFinal(Utf8.encode(rawPassword.toString()));

            result = Base64.getEncoder().encodeToString(macData);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            log.error("Error password encode", e);
        }

        return result;
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }

        String encodedRawPass = encode(rawPassword);

        return MessageDigest.isEqual(Utf8.encode(encodedRawPass), Utf8.encode(encodedPassword));
    }
}
