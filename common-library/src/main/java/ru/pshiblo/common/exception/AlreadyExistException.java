package ru.pshiblo.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Maxim Pshiblo
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AlreadyExistException extends ApelsinException {
    public AlreadyExistException() {
        super();
    }

    public AlreadyExistException(String message) {
        super(message);
    }
}
