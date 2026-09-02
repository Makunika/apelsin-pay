package ru.pshiblo.auth;

import ru.pshiblo.auth.clients.dto.PersonInfo;
import ru.pshiblo.auth.clients.dto.Role;
import ru.pshiblo.auth.clients.dto.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.List;

final class AuthTestSupport {

    static final String LOGIN = "mpshiblo3";
    static final String PASSWORD = "1234";
    static final int USER_ID = 42;

    private AuthTestSupport() {
    }

    static User user(String passwordHash) {
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");
        User user = new User();
        user.setId(USER_ID);
        user.setLogin(LOGIN);
        user.setPasswordHash(passwordHash);
        user.setRoles(List.of(role));
        return user;
    }

    static PersonInfo personInfo() {
        PersonInfo info = new PersonInfo();
        info.setUserId((long) USER_ID);
        info.setFirstName("Максим");
        info.setLastName("Пшибло");
        info.setEmail("test@example.com");
        info.setStatus("CONFIRMED");
        info.setLock(false);
        return info;
    }

    /** code_challenge = BASE64URL(SHA256(code_verifier)) */
    static String s256(String codeVerifier) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
