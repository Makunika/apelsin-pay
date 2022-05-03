package ru.pshiblo.transaction.tinkoff.model.response;

import lombok.Data;

@Data
public class TinkoffInvoicingResponse {
    private String id;
    private String paymentUrl;
}
