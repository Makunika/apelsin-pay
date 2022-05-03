package ru.pshiblo.payment.service;

import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.security.model.AuthUser;

public interface PayService {
    Order payInner(long orderId, AuthUser user, String accountNumber);
    Order payTinkoff(long orderId);
    void paySuccessByTinkoff(long orderId);
}
