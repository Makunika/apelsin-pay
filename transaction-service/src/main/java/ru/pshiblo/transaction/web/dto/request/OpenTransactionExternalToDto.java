package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.model.PayoutModel;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
public class OpenTransactionExternalToDto {
    private BigDecimal money;
    private String fromNumber;
    private PayoutModel payoutModel;
}
