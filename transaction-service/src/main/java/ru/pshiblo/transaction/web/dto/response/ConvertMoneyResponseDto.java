package ru.pshiblo.transaction.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertMoneyResponseDto {
    private Currency currency;
    private BigDecimal amount;
}
