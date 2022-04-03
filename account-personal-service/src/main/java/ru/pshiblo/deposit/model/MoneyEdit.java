package ru.pshiblo.deposit.model;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
public class MoneyEdit {
    private String accountNumber;
    private BigDecimal balanceEdit;
    private Currency currency;
}
