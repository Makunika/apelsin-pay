package ru.pshiblo.payment.domain;

import lombok.*;
import org.hibernate.Hibernate;
import ru.pshiblo.payment.enums.Currency;
import ru.pshiblo.payment.enums.OrderStatus;
import ru.pshiblo.payment.enums.OrderType;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    private String shortName;
    private String fullName;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING)
    private Currency currency;
    private Long companyId;
    private Long transactionId;
    private OrderType orderType;
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    private String reasonCancel;
    private String redirectUrl;
    private String payUrl;
    private LocalDateTime endDate;
    private String accountNumberTo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Order order = (Order) o;
        return id != null && Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
