package ru.pshiblo.payment.web.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PayInnerDto {
    private long orderId;
    @NotNull
    @NotBlank
    private String accountNumberFrom;
}
