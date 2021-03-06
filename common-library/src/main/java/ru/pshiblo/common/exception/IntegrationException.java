package ru.pshiblo.common.exception;

import lombok.Getter;

@Getter
public class IntegrationException extends RuntimeException {
    private final int status;
    private final String message;

    public IntegrationException(int status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public IntegrationException(IntegrationException e) {
        super(e.getMessage());
        this.message = e.getMessage();
        this.status = e.getStatus();
    }

}
