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
public class CreateBusinessAccountTypeDto {
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Currency currency;
    @DecimalMin("100.0")
    private BigDecimal maxSumForTransfer;
    @DecimalMin("0.0")
    @DecimalMax("0.9")
    private BigDecimal commissionRateWithdraw;
}
