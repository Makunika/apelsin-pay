package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
public class MoneyDto {
    private String accountNumber;
    private BigDecimal balanceEdit;
    private Currency currency;
}
