package ru.pshiblo.account.personal.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pshiblo.account.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonalAccountTypeDto implements Serializable {
    private String name;
    private String description;
    private Currency currency;
    private String categoryToCashBack;
    private int percentCashback;
    private BigDecimal maxSum;
    private BigDecimal maxSumForPay;
    private boolean requiredToFirstPay;
    private BigDecimal minSumToStartWork;
    private boolean typeRequiredConfirmed;
}
