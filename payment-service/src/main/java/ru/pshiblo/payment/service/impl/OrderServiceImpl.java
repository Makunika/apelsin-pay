package ru.pshiblo.payment.service.impl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.payment.clients.InfoBusinessClient;
import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.payment.repository.OrderRepository;
import ru.pshiblo.payment.service.OrderService;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;
    private final InfoBusinessClient infoBusinessClient;
    private final RabbitTemplate rabbitTemplate;
    @Value("${pay.url}")
    private String payUrl;

    @Override
    public Order create(Order order, String apiKey) {
        Assert.notNull(order.getAmount(), "Amount");
        Assert.notNull(order.getCurrency(), "Currency");
        Assert.notNull(order.getCompanyId(), "CompanyId");
        Assert.notNull(order.getFullName(), "FullName");
        Assert.notNull(order.getShortName(), "ShortName");
        Assert.notNull(order.getRedirectUrl(), "RedirectUrl");
        Assert.notNull(order.getEndDate(), "EndDate");
        Assert.notNull(order.getAccountNumberTo(), "AccountNumberTo");

        checkAuthCompany(order.getCompanyId(), apiKey);

        order.setOrderStatus(OrderStatus.CREATED);
        Order savedOrder = repository.save(order);
        savedOrder.setPayUrl(payUrl + "?orderId=" + savedOrder.getId());
        return repository.save(savedOrder);
    }

    @Override
    public Order confirm(long orderId, String apiKey) {
        Order order = findById(orderId);
        checkAuthCompany(order.getCompanyId(), apiKey);

        if (order.getOrderStatus() != OrderStatus.HOLD) {
            throw new IllegalArgumentException("order not in state HOLD");
        }
        order.setOrderStatus(OrderStatus.COMPLETED);
        rabbitTemplate.convertAndSend("transaction.unhold", order.getTransactionId());
        return repository.save(order);
    }

    @Override
    public Order cancel(long orderId, String apiKey) {
        Order order = findById(orderId);
        checkAuthCompany(order.getCompanyId(), apiKey);

        if (order.getOrderStatus() != OrderStatus.HOLD) {
            throw new IllegalArgumentException("order not in state HOLD");
        }
        order.setOrderStatus(OrderStatus.CANCEL);
        rabbitTemplate.convertAndSend("transaction.cancel", new TransactionError(
                "Отменен магазином",
                order.getTransactionId().intValue()
        ));
        return repository.save(order);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TransactionError {
        private String reason;
        private Integer transactionId;
    }

    @Override
    public OrderStatus getStatus(long orderId, String apiKey) {
        Order order = findById(orderId);
        checkAuthCompany(order.getCompanyId(), apiKey);
        return order.getOrderStatus();
    }

    @Override
    public Order findById(long orderId) {
        return repository.findById(orderId).orElseThrow();
    }

    @Override
    public Order findById(long orderId, String apiKey) {
        Order order = findById(orderId);
        checkAuthCompany(order.getCompanyId(), apiKey);
        return order;
    }

    @Override
    @Transactional
    public Order save(Order order) {
        return repository.save(order);
    }

    @Override
    public boolean isExpired(Order order) {
        return order.getEndDate().isBefore(LocalDateTime.now());
    }

    @Override
    public Order findByTransactionId(long id) {
        Optional<Order> order = repository.findByTransactionId(id);
        return order.orElseThrow();
    }

    private void checkAuthCompany(long companyId ,String apiKey) {
        if (Boolean.FALSE.equals(infoBusinessClient.checkApiKey(companyId, apiKey).getBody())) {
            throw new NotAllowedOperationException();
        }
    }
}
