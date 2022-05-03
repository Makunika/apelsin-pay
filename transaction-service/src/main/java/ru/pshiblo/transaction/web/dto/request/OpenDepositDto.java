package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OpenDepositDto {
    @NotNull
    @NotBlank
    private String accountNumberTo;
    @DecimalMin("0")
    private BigDecimal money;
}
