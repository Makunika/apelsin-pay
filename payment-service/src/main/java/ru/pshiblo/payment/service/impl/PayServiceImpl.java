package ru.pshiblo.payment.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.payment.clients.TransactionClient;
import ru.pshiblo.payment.clients.model.OpenPaymentInnerDto;
import ru.pshiblo.payment.clients.model.Transaction;
import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.payment.enums.OrderType;
import ru.pshiblo.payment.service.OrderService;
import ru.pshiblo.payment.service.PayService;
import ru.pshiblo.security.model.AuthUser;

@Service
@Slf4j
@RequiredArgsConstructor
public class PayServiceImpl implements PayService {

    private final TransactionClient transactionClient;
    private final OrderService orderService;

    @Override
    public Order payInner(long orderId, AuthUser user, String accountNumber) {
        Order order = orderService.findById(orderId);

        if (orderService.isExpired(order)) {
            throw new IllegalArgumentException("Order is expired");
        }

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new IllegalArgumentException("Order not in status CREATED");
        }

        order.setOrderType(OrderType.INNER);

        OpenPaymentInnerDto request = new OpenPaymentInnerDto();
        request.setCurrency(order.getCurrency());
        request.setMoney(order.getAmount());
        request.setUserId(user.getId());
        request.setAccountNumberTo(order.getAccountNumberTo());
        request.setAccountNumberFrom(accountNumber);

        Transaction transaction = transactionClient.openPaymentInner(request);
        order.setOrderStatus(OrderStatus.PROGRESS);
        order.setTransactionId(transaction.getId().longValue());
        return orderService.save(order);
    }

    @Override
    public String payTinkoff(long orderId) {
        //TODO: оплата через тинькофф
        return null;
    }
}
