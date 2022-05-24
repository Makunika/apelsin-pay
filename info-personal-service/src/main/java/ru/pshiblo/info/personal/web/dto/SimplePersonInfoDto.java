package ru.pshiblo.info.personal.web.dto;

import lombok.Data;

@Data
public class SimplePersonInfoDto {
    private long personInfoId;
    private long userId;
    private String name;
    private String login;
}
