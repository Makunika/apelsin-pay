package ru.pshiblo.transaction.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Maxim Pshiblo
 */
@Getter
@RequiredArgsConstructor
public enum TransactionStatus {
    START_OPEN(false, false),
    END_OPEN(false, false),
    START_COMMISSION(false, false),
    END_COMMISSION(false, false),
    START_FROM_CHECK(false, false),
    START_TO_CHECK(true, false),
    START_SEND(false, false),
    START_APPLY_PAYMENT(true, false),
    END_APPLY_PAYMENT(true, false),
    START_ADD_MONEY(true, false),
    END_SEND(true, false),
    CLOSED(true, true),
    CANCELED(false, false),
    END_ADD_MONEY(true, true),
    HOLD(true, true);

    private final boolean isWithdrawnMoney;
    private final boolean isDepositedMoney;
}
