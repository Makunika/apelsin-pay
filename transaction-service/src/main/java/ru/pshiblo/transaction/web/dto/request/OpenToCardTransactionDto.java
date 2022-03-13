package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.transaction.enums.Currency;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
public class OpenToCardTransactionDto {
    private String toCardNumber;
    private BigDecimal money;
    private String fromAccountNumber;
    private Currency currency;
    private int cvc;
}
