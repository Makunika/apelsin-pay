package ru.pshiblo.payment.web.dto.response;

import lombok.Data;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.payment.enums.OrderType;

import java.io.Serializable;

@Data
public class OrderStatusResponseDto implements Serializable {
    private final OrderType orderType;
    private final OrderStatus orderStatus;
}
