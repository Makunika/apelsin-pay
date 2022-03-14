package ru.pshiblo.account.domain;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.account.enums.CardPayType;
import ru.pshiblo.account.enums.CardType;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Table(name = "cards")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
    private Boolean lockCard = false;

    @Column(name = "pin", nullable = false)
    private String pin;

    @CreatedDate
    private LocalDateTime created;

    @LastModifiedDate
    private LocalDateTime updated;
}