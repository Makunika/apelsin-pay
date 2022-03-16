package ru.pshiblo.account.exceptions;

/**
 * @author Maxim Pshiblo
 */
public class TransactionNotAllowedException extends RuntimeException {
    public TransactionNotAllowedException() {
        super();
    }

    public TransactionNotAllowedException(String message) {
        super(message);
    }
}
