package ru.pshiblo.transaction.exceptions;

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
