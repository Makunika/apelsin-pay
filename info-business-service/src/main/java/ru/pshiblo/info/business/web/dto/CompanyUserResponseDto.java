package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.info.business.enums.RoleCompany;

import java.io.Serializable;

@Data
public class CompanyUserResponseDto implements Serializable {
    private final Long id;
    private final long userId;
    private final RoleCompany roleCompany;
    private final CompanyResponseDto company;
}
