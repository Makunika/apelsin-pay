package ru.pshiblo.deposit.web.dto.response;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DepositResponseDto {
    private String number;
    private BigDecimal balance;
    private Currency currency;
    private boolean isEnabled;
    private LocalDate startDepositDate;
    private int months;
    private int userId;
    private boolean validDepositType;
    private int depositTypeId;
    private boolean requiredToPayment;
}
