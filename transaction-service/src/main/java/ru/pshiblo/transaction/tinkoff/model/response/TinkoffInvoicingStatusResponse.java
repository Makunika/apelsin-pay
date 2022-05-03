package ru.pshiblo.transaction.tinkoff.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class TinkoffInvoicingStatusResponse {
    private Status status;

    @Getter
    @RequiredArgsConstructor
    public static enum Status {
        CREATED(true),
        CONFIRMED(false),
        REJECTED(false),
        HOLD(true),
        PROCESS(true),
        PAID(true),
        FAILED(false),
        FAILED_AFTER_HOLD(true),
        CANCELED(true),
        FAILED_REFUND(true),
        REFUNDED(true),
        PARTIAL_REFUNDED(true),
        ;

        private final boolean success;
    }
}
