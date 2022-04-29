package ru.pshiblo.payment.service;

import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.security.model.AuthUser;

public interface OrderService {
    Order create(Order order, String apiKey);
    Order confirm(long orderId, String apiKey);
    Order cancel(long orderId, String apiKey);
    OrderStatus getStatus(long orderId, String apiKey);
    Order findById(long orderId);
    Order findById(long orderId, String apiKey);
    Order save(Order order);
    boolean isExpired(Order order);
    Order findByTransactionId(long id);
}
