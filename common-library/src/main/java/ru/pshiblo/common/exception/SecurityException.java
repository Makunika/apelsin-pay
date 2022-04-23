package ru.pshiblo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Maxim Pshiblo
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class SecurityException extends ApelsinException {
    public SecurityException() {
        super();
    }

    public SecurityException(String message) {
        super(message);
    }
}
