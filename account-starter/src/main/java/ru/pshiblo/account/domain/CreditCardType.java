package ru.pshiblo.account.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.account.enums.Currency;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name= "credit_card_type")
@EntityListeners(AuditingEntityListener.class)
public class CreditCardType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private int maxMonths;
    private int minMonths;
    private int daysForDeposit;
    private int percentMonth;
    private Currency currency;
    private int percentYear;
    private BigDecimal minSum;
    private boolean isValid = true;
    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime updated;
}
