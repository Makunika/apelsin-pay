package ru.pshiblo.deposit.web.dto.request;

import lombok.Data;

@Data
public class CreateDepositDto {
    private int typeId;
    private int userId;
}
