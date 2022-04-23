package ru.pshiblo.transaction.tinkoff.model.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TinkoffPayment {
    private String id;
    private TinkoffFrom from;
    private TinkoffTo to;
    private String purpose;
    private BigDecimal amount;
}
