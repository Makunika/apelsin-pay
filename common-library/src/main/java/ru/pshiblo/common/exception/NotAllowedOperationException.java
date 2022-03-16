package ru.pshiblo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotAllowedOperationException extends SecurityException {
    public NotAllowedOperationException() {
        super();
    }

    public NotAllowedOperationException(String message) {
        super(message);
    }
}
