package ru.pshiblo.transaction.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.account.enums.Currency;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transactions_history")
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TransactionStatus status;

    @Column(name = "routing_to")
    private String route;

    @Column(name = "to_number", nullable = false, length = 100)
    private String toNumber;

    @Column(name = "from_number", nullable = false, length = 100)
    private String fromNumber;

    @Column(name = "is_inner", nullable = false)
    private boolean isInner = false;

    @Column(name = "is_to_card", nullable = false)
    private boolean isToCard = false;

    @Column(name = "commission")
    private BigDecimal commissionRate;

    @Column(name = "commission_value")
    private BigDecimal commissionValue;

    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;

    @Column(name = "reason_cancel")
    private String reasonCancel;

    @Column(name = "to_user_id")
    private Integer toUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_from", length = 3)
    private Currency currencyFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_to", length = 3)
    private Currency currencyTo;

    @Column(name = "money", nullable = false)
    private BigDecimal money;

    @Column(name = "money_with_commision")
    private BigDecimal moneyWithCommission;

    @Column(name = "owner_username", nullable = false)
    private String ownerUsername;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem = false;

    @Column(name = "is_approve", nullable = false)
    private boolean isApprove = false;

    @Column(name = "account_type")
    private AccountType accountType;
}
