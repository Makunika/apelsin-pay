package ru.pshiblo.transaction.domain;

import lombok.Data;
import ru.pshiblo.transaction.enums.CardPayType;
import ru.pshiblo.transaction.enums.CardType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDate;

@Data
@Table(name = "cards")
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "number", nullable = false, unique = true)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private CardType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_pay", nullable = false, length = 50)
    private CardPayType typePay;

    @ManyToOne(optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "cvc", nullable = false)
    private Integer cvc;

    @Column(name = "expired", nullable = false)
    private LocalDate expired;

    @Column(name = "lock", nullable = false)
    private Boolean lock = false;

    @Column(name = "pin", nullable = false)
    private String pin;
}