package ru.pshiblo.payment.clients.model;

import lombok.Data;
import ru.pshiblo.payment.enums.Currency;

import java.math.BigDecimal;

@Data
public class BusinessAccount {
    private String number;
    private boolean lock;
    private int companyId;
    private long typeId;
    private String typeName;
    private BigDecimal balance;
    private Currency currency;
}
