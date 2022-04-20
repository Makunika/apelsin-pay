package ru.pshiblo.account.personal.web.dto.request;

import lombok.Data;

@Data
public class CreateAccountPersonalDto {
    private int typeId;
    private int userId;
}
