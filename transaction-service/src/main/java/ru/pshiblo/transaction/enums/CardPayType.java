package ru.pshiblo.transaction.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Maxim Pshiblo
 */
@Getter
@RequiredArgsConstructor
public enum CardPayType {
    VISA(4),
    MASTER_CARD(5);

    private final int number;
}
