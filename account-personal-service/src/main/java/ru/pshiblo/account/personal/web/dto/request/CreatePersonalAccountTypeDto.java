package ru.pshiblo.account.personal.web.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pshiblo.account.enums.Currency;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonalAccountTypeDto implements Serializable {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Currency currency;
    @DecimalMin("1000.0")
    @NotNull
    private BigDecimal maxSum;
    @DecimalMin("1.0")
    @NotNull
    private BigDecimal maxSumForPay;
    @DecimalMin("1.0")
    private BigDecimal minSumToStartWork;
    private boolean typeRequiredConfirmed;
}
