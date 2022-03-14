package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
public class OpenToAccountTransactionDto {
    private String toAccountNumber;
    private BigDecimal money;
    private String fromAccountNumber;
    private Currency currency;
}
