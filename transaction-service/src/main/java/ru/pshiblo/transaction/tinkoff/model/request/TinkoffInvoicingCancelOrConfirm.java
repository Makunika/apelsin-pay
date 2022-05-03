package ru.pshiblo.transaction.tinkoff.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TinkoffInvoicingCancelOrConfirm {
    private BigDecimal amount;
}
