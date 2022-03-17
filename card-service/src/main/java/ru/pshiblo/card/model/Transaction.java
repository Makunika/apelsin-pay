package ru.pshiblo.card.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonIgnoreProperties
@Data
public class Transaction implements Serializable {
    private Integer id;
    private LocalDateTime created;
    private LocalDateTime updated;
    private String status;
    private String toNumber;
    private String toCardNumber;
    private String fromNumber;
    private String fromCardNumber;
    private boolean isInner;
    private BigDecimal commissionRate;
    private BigDecimal commissionValue;
    private Integer ownerUserId;
    private String ownerUsername;
    private String reasonCancel;
    private boolean isSystem;
    private Integer toUserId;
    private Currency currency;
    private Currency currencyFrom;
    private Currency currencyTo;
    private BigDecimal money;
    private BigDecimal moneyWithCommission;
    private boolean isApproveSend;
    private boolean isApproveAddMoney;
    private AccountType accountTypeFrom;
    private AccountType accountTypeTo;
}