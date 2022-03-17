package ru.pshiblo.transaction.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "transactions")
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TransactionStatus status;

    @Column(name = "to_number", nullable = false, length = 100)
    private String toNumber;

    @Column(name = "to_card_number", length = 100)
    private String toCardNumber;

    @Column(name = "from_number", nullable = false, length = 100)
    private String fromNumber;

    @Column(name = "from_card_number", length = 100)
    private String fromCardNumber;

    @Column(name = "is_inner", nullable = false)
    private boolean isInner = false;

    @Column(name = "commission")
    private BigDecimal commissionRate;

    @Column(name = "commission_value")
    private BigDecimal commissionValue;

    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;

    @Column(name = "owner_username", nullable = false)
    private String ownerUsername;

    @Column(name = "reason_cancel")
    private String reasonCancel;

    @Column(name = "is_system", nullable = false)
    private boolean isSystem = false;

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

    @Column(name = "is_approve_send", nullable = false)
    private boolean isApproveSend = false;

    @Column(name = "is_approve_add_money", nullable = false)
    private boolean isApproveAddMoney = false;

    @Column(name = "account_type_from")
    private AccountType accountTypeFrom;

    @Column(name = "account_type_to")
    private AccountType accountTypeTo;
}