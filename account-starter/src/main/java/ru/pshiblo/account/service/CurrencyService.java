package ru.pshiblo.account.service;

import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

public interface CurrencyService {
    BigDecimal convertMoney(Currency from, Currency to, BigDecimal money);
}
