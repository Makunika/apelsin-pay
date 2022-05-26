package ru.pshiblo.transaction.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionResponseDto implements Serializable {
    private Integer id;
    private LocalDateTime created;
    @NotNull
    private TransactionType type;
    @NotNull
    private TransactionStatus status;
    private String toNumber;
    private String fromNumber;
    @NotNull
    private String ownerUsername;
    @NotNull
    private boolean isInnerFrom = false;
    @NotNull
    private boolean isInnerTo = false;
    private BigDecimal commissionRate;
    private BigDecimal commissionValue;
    private String reasonCancel;
    @NotNull
    private boolean isSystem = false;
    @NotNull
    private Currency currency;
    private Currency currencyFrom;
    private Currency currencyTo;
    @NotNull
    private BigDecimal money;
    private BigDecimal moneyWithCommission;
    private String tinkoffPayUrl;
}
