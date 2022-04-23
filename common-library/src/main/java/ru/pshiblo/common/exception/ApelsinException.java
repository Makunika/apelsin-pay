package ru.pshiblo.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApelsinException extends RuntimeException {

    private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public ApelsinException() {
    }

    public ApelsinException(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ApelsinException(String message) {
        super(message);
    }

    public ApelsinException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApelsinException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApelsinException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public ApelsinException(Throwable cause) {
        super(cause);
    }

    public ApelsinException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
