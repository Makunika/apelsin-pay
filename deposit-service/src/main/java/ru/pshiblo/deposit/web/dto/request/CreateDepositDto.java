package ru.pshiblo.deposit.web.dto.request;

import lombok.Data;

@Data
public class CreateDepositDto {
    private int depositTypeId;
    private int userId;
    private int months;
}
