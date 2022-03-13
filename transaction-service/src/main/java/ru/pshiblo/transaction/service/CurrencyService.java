package ru.pshiblo.transaction.service;

import ru.pshiblo.transaction.enums.Currency;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal convertMoney(Currency from, Currency to, BigDecimal money);
}
