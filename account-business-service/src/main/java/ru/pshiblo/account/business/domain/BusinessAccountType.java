package ru.pshiblo.account.business.domain;

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
@Table(name= "business_account_type")
@EntityListeners(AuditingEntityListener.class)
public class BusinessAccountType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String name;
    private String description;
    private Currency currency;
    private BigDecimal maxSumForTransfer;
    private BigDecimal commissionRateWithdraw;
    private boolean isValid = true;
    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime updated;
}
