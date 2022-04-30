package ru.pshiblo.account.business.web.dto.response;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BusinessAccountTypeResponseDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String description;
    private final Currency currency;
    private final BigDecimal maxSumForTransfer;
    private final BigDecimal commissionRateWithdraw;
    private final boolean isValid;
}
