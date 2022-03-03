package ru.pshiblo.transaction.domain;

import lombok.Data;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Table(name = "transactions")
@Entity
@Data
public class Transaction implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TransactionStatus status;

    @Column(name = "to_number", nullable = false, length = 100)
    private String toNumber;

    @Column(name = "from_number", nullable = false, length = 100)
    private String fromNumber;

    @Column(name = "is_inner", nullable = false)
    private boolean isInner = false;

    @Column(name = "is_to_card", nullable = false)
    private boolean isToCard = false;

    @Column(name = "commission")
    private BigDecimal commission;

    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;

    @Column(name = "reason_cancel")
    private String reasonCancel;

    @Column(name = "to_user_id")
    private Integer toUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "money", nullable = false)
    private BigDecimal money;
}