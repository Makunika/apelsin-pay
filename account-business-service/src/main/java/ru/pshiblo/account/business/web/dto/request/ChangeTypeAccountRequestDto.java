package ru.pshiblo.account.business.web.dto.request;

import lombok.Data;

@Data
public class ChangeTypeAccountRequestDto {
    private int typeId;
    private String number;
}
