package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.info.business.enums.RoleCompany;

import javax.validation.constraints.NotNull;

@Data
public class DeleteUserFromCompanyDto {
    private long companyId;
    private long userId;
}
