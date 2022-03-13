package ru.pshiblo.transaction.domain;

import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;

import javax.persistence.*;
import java.math.BigDecimal;

@Table(name = "transactions_history")
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
public class TransactionHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

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

    public static TransactionHistory fromTransaction(Transaction transaction) {
        TransactionHistory history = new TransactionHistory();
        history.setTransaction(transaction);
        history.status = transaction.getStatus();
        history.toNumber = transaction.getToNumber();
        history.fromNumber = transaction.getFromNumber();
        history.isInner = transaction.isInner();
        history.isToCard = transaction.isToCard();
        history.commissionRate = transaction.getCommissionRate();
        history.ownerUserId = transaction.getOwnerUserId();
        history.reasonCancel = transaction.getReasonCancel();
        history.toUserId = transaction.getToUserId();
        history.currency = transaction.getCurrency();
        history.money = transaction.getMoney();
        history.currencyFrom = transaction.getCurrencyFrom();
        history.currencyTo = transaction.getCurrencyTo();
        history.moneyWithCommission = transaction.getMoneyWithCommission();
        history.commissionValue = transaction.getCommissionValue();
        return history;
    }
}