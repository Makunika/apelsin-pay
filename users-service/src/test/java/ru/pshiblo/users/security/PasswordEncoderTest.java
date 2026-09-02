package ru.pshiblo.users.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.pshiblo.common.security.ApelsinPasswordEncoders;
import ru.pshiblo.common.security.Hmac512PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderTest {

    private static final String SALT = "test-salt";
    private final PasswordEncoder encoder = ApelsinPasswordEncoders.createDelegatingPasswordEncoder(SALT);

    @Test
    void newHash_isBcryptWithPrefix() {
        String hash = encoder.encode("1234");

        assertThat(hash).startsWith(ApelsinPasswordEncoders.BCRYPT_PREFIX);
        assertThat(encoder.matches("1234", hash)).isTrue();
        assertThat(encoder.matches("wrong", hash)).isFalse();
        assertThat(encoder.upgradeEncoding(hash)).isFalse();
    }

    @Test
    void legacyHmacHash_withoutPrefix_matchesAndNeedsUpgrade() {
        String legacy = new Hmac512PasswordEncoder(SALT).encode("1234");
        assertThat(legacy).doesNotStartWith("{");

        assertThat(encoder.matches("1234", legacy)).isTrue();
        assertThat(encoder.matches("wrong", legacy)).isFalse();
        assertThat(encoder.upgradeEncoding(legacy)).isTrue();
    }

    @Test
    void legacyHmacHash_withExplicitPrefix_matches() {
        String legacy = "{" + ApelsinPasswordEncoders.HMAC512_ID + "}" + new Hmac512PasswordEncoder(SALT).encode("1234");

        assertThat(encoder.matches("1234", legacy)).isTrue();
        assertThat(encoder.upgradeEncoding(legacy)).isTrue();
    }
}
