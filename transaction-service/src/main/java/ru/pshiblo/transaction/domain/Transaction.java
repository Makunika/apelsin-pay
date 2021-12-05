package ru.pshiblo.transaction.domain;

import lombok.Data;
import ru.pshiblo.kafka.error.annotation.EnableErrorTopic;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;

@Table(name = "transactions")
@Entity
@Data
@EnableErrorTopic(errorTopic = "transaction.error", maxAttempt = 1)
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "commission", nullable = false)
    private BigDecimal commission;

    @Column(name = "owner_user_id", nullable = false)
    private Integer ownerUserId;

    @Column(name = "to_user_id")
    private Integer toUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "money", nullable = false)
    private BigDecimal money;
}