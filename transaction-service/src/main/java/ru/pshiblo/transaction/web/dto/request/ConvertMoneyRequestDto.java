package ru.pshiblo.transaction.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertMoneyRequestDto {
    private Currency currencyTo;
    private Currency currencyFrom;
    private BigDecimal amount;
}
