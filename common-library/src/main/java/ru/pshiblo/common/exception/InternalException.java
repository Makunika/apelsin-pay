package ru.pshiblo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Maxim Pshiblo
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalException extends ApelsinException {
    public InternalException() {
        super();
    }

    public InternalException(String message) {
        super(message);
    }

    public InternalException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
