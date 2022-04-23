package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.info.business.enums.RoleCompany;

import javax.validation.constraints.NotNull;

@Data
public class AddUserToCompanyDto {
    private long companyId;
    private long userId;
    @NotNull
    private RoleCompany roleCompany;
}
