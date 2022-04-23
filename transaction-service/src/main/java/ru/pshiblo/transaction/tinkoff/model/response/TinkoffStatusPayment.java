package ru.pshiblo.transaction.tinkoff.model.response;

import lombok.Data;

@Data
public class TinkoffStatusPayment {
    private String status;
    private String errorMessage;
}
