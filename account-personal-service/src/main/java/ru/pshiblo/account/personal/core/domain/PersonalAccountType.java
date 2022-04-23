package ru.pshiblo.account.personal.core.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.account.enums.Currency;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
@Table(name= "personal_account_type")
@EntityListeners(AuditingEntityListener.class)
public class PersonalAccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Currency currency;
    private String categoryToCashBack;
    private int percentCashback;
    private BigDecimal maxSum;
    private BigDecimal maxSumForPay;
    private boolean requiredToFirstPay;
    private BigDecimal minSumToStartWork;
    private boolean typeRequiredConfirmed;
    private boolean isValid;
    @CreatedDate
    private LocalDateTime created;
    @CreatedBy
    private String createdBy;
    @LastModifiedDate
    private LocalDateTime updated;
    @LastModifiedBy
    private String updatedBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PersonalAccountType that = (PersonalAccountType) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
