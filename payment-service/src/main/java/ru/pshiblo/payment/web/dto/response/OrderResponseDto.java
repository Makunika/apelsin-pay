package ru.pshiblo.payment.web.dto.response;

import lombok.Data;
import ru.pshiblo.payment.enums.Currency;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.payment.enums.OrderType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderResponseDto implements Serializable {
    private final Long id;
    private final String shortName;
    private final String fullName;
    private final BigDecimal amount;
    private final Currency currency;
    private final Long companyId;
    private final Long transactionId;
    private final OrderType orderType;
    private final OrderStatus orderStatus;
    private final String reasonCancel;
    private final String redirectUrl;
    private final String payUrl;
    private final String payTinkoffUrl;
    private final LocalDateTime endDate;
    private final String accountNumberTo;
}
