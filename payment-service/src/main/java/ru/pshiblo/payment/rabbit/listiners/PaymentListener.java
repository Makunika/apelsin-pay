package ru.pshiblo.payment.rabbit.listiners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.pshiblo.payment.clients.model.Transaction;
import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.payment.service.OrderService;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentListener {

    private final OrderService service;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.hold",
                    value = @Queue("transaction.hold_payment_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            )
    )
    public void afterHoldListener(@Payload Transaction transaction) {
        if (transaction.getType().equals("PAYMENT")) {
            try {

                Order order = service.findByTransactionId(transaction.getId());
                order.setOrderStatus(OrderStatus.HOLD);
                service.save(order);
                //TODO: webSocket
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.cancel.after",
                    value = @Queue("transaction.cancel.after_payment_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            )
    )
    public void afterCancelListener(@Payload Transaction transaction) {
        if (transaction.getType().equals("PAYMENT")) {
            try {

                Order order = service.findByTransactionId(transaction.getId());
                order.setOrderStatus(OrderStatus.CANCEL);
                order.setReasonCancel(transaction.getReasonCancel());
                service.save(order);
                //TODO: webSocket
            } catch (Exception e) {
                log.warn(e.getMessage(), e);
            }
        }
    }
}
