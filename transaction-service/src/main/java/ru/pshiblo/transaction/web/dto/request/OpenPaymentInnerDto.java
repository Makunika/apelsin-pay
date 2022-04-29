package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OpenPaymentInnerDto {
    @NotNull
    private long userId;
    @NotNull
    @NotBlank
    private String accountNumberFrom;
    @NotNull
    @NotBlank
    private String accountNumberTo;
    @NotNull
    private Currency currency;
    @DecimalMin("0")
    private BigDecimal money;
}
