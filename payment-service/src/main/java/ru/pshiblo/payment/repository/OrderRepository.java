package ru.pshiblo.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.payment.domain.Order;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByTransactionId(Long transactionId);
}