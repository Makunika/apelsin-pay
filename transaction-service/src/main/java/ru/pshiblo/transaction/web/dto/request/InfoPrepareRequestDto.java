package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;

@Data
public class InfoPrepareRequestDto {
    private String toNumber;
    private String fromNumber;
    private BigDecimal money;
    private Currency currency;
}
