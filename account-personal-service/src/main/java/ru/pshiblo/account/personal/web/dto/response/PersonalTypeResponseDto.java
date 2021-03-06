package ru.pshiblo.account.personal.web.dto.response;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class PersonalTypeResponseDto implements Serializable {
    private final Integer id;
    private final String name;
    private final String description;
    private final Currency currency;
    private final String categoryToCashBack;
    private final int percentCashback;
    private final BigDecimal maxSum;
    private final BigDecimal maxSumForPay;
    private final boolean requiredToFirstPay;
    private final BigDecimal minSumToStartWork;
    private final boolean typeRequiredConfirmed;
    private final boolean isValid;
}
