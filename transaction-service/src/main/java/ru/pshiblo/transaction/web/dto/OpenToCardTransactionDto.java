package ru.pshiblo.transaction.web.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
public class OpenToCardTransactionDto {
    private String toCardNumber;
    private BigDecimal money;
    private String fromCardNumber;
    private int cvc;
}
