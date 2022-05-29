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
    private String shortName;
    @NotNull
    @NotBlank
    private String fullName;
    @NotNull
    @DecimalMin("0.5")
    @DecimalMax("99999")
    private BigDecimal amount;
    @NotNull
    private Currency currency;
    @NotNull
    private Long companyId;
    @NotNull
    @NotBlank
    private String redirectUrl;
    @NotNull
    @Future
    private LocalDateTime endDate;
}
