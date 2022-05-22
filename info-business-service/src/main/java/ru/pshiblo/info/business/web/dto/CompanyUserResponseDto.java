package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.info.business.enums.RoleCompany;

import java.io.Serializable;

@Data
public class CompanyUserResponseDto implements Serializable {
    private Long id;
    private long userId;
    private RoleCompany roleCompany;
    private CompanyResponseDto company;
}
