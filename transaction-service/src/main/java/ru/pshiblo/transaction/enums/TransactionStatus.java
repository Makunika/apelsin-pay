package ru.pshiblo.transaction.enums;

/**
 * @author Maxim Pshiblo
 */
public enum TransactionStatus {
    START_OPEN,
    END_OPEN,
    START_COMMISSION,
    END_COMMISSION,
    START_SEND,
    END_SEND,
    CLOSED,
    CANCELED;
}
