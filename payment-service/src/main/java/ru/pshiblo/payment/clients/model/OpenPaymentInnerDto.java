package ru.pshiblo.payment.clients.model;

import lombok.Data;
import ru.pshiblo.payment.enums.Currency;

import java.math.BigDecimal;

@Data
public class OpenPaymentInnerDto {
    private long userId;
    private String accountNumberFrom;
    private String accountNumberTo;
    private Currency currency;
    private BigDecimal money;
}
