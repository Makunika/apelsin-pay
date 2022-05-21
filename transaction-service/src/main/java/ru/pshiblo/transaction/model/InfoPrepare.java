package ru.pshiblo.transaction.model;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
public class InfoPrepare {
    private String nameTo;
    private BigDecimal money;
    private Currency currency;
    private BigDecimal moneyFrom;
    private Currency currencyFrom;
    private BigDecimal moneyTo;
    private Currency currencyTo;
}
