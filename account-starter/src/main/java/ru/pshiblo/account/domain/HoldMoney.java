package ru.pshiblo.account.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "hold_money")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class HoldMoney {
    @Id
    @GeneratedValue
    private Long id;
    private BigDecimal amount;
    private LocalDateTime holdUntil;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        HoldMoney holdMoney = (HoldMoney) o;
        return id != null && Objects.equals(id, holdMoney.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
