package ru.pshiblo.transaction.domain;

import lombok.Data;
import ru.pshiblo.transaction.enums.AccountType;
import ru.pshiblo.transaction.enums.Currency;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Data
@Table(name = "accounts")
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(name = "number", nullable = false)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccountType type;

    @Column(name = "balance", nullable = false)
    private BigDecimal balance;

    @Column(name = "lock", nullable = false)
    private Boolean lock = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false)
    private Currency currency;
}