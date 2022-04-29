package ru.pshiblo.payment.web.dto.request;

import lombok.Data;
import ru.pshiblo.payment.enums.Currency;
import ru.pshiblo.payment.enums.OrderType;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CreateOrderDto implements Serializable {
    private final String shortName;
    @NotNull
    @NotBlank
    private final String fullName;
    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("99999")
    private final BigDecimal amount;
    @NotNull
    private final Currency currency;
    @NotNull
    private final Long companyId;
    @NotNull
    @NotBlank
    private final String redirectUrl;
    @NotNull
    @Future
    private final LocalDateTime endDate;
    @NotNull
    @NotBlank
    private final String accountNumberTo;
}
