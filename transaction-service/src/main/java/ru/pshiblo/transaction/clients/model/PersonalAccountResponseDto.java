package ru.pshiblo.transaction.clients.model;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
public class PersonalAccountResponseDto {
    private String number;
    private BigDecimal balance;
    private Currency currency;
    private boolean isLock;
    private long userId;
    private boolean validType;
    private long typeId;
    private String typeName;
}
