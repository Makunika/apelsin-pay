package ru.pshiblo.maxbank.security.jwt.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @author Maxim Pshiblo
 */
public class JwtAuthenticationException extends AuthenticationException {
    public JwtAuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
