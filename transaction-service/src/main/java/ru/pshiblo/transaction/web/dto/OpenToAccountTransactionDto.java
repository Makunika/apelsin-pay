package ru.pshiblo.transaction.web.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Data
public class OpenToAccountTransactionDto {
    private String toAccountNumber;
    private BigDecimal money;
    private String fromCardNumber;
    private int cvc;
}
