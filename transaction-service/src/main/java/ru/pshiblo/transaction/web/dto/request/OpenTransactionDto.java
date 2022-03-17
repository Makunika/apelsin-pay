package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
public class OpenTransactionDto {
    private String toNumber;
    private BigDecimal money;
    private String fromNumber;
    private Currency currency;
}
