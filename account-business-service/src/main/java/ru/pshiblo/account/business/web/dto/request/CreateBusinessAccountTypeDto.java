package ru.pshiblo.account.business.web.dto.request;

import lombok.Data;
import org.hibernate.validator.constraints.Range;
import ru.pshiblo.account.enums.Currency;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CreateBusinessAccountTypeDto implements Serializable {
    @NotBlank
    private final String name;
    @NotBlank
    private final String description;
    @NotNull
    private final Currency currency;
    @NotNull
    @DecimalMin("100.0")
    private final BigDecimal maxSumForTransfer;
    @DecimalMin("0.0")
    @DecimalMax("0.9")
    private final BigDecimal commissionRateWithdraw;
}
