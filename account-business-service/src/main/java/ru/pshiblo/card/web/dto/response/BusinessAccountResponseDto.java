package ru.pshiblo.card.web.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BusinessAccountResponseDto {
    private String number;
    private boolean lock;
    private int companyId;
    private long typeId;
    private String typeName;
    private BigDecimal balance;
}
