package ru.pshiblo.deposit.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;

import javax.persistence.*;
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
    private String fromNumber;
    private boolean isInnerFrom;
    private boolean isInnerTo;
    private String additionInfoTo;
    private String additionInfoFrom;
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