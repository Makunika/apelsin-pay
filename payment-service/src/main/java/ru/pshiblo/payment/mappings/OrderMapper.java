package ru.pshiblo.payment.mappings;

import org.mapstruct.Mapper;
import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.payment.web.dto.request.CreateOrderDto;
import ru.pshiblo.payment.web.dto.response.OrderResponseDto;
import ru.pshiblo.payment.web.dto.response.OrderStatusResponseDto;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponseDto toDTO(Order order);
    Order toEntity(CreateOrderDto createOrderDto);
    OrderStatusResponseDto toOrderStatus(Order order);
}
