package ru.pshiblo.info.business.services;

import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.enums.RoleCompany;
import ru.pshiblo.info.business.web.dto.CompanyResponseDto;
import ru.pshiblo.security.enums.ConfirmedStatus;
import ru.pshiblo.security.model.AuthUser;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    Company create(Company company, AuthUser user);
    void delete(long companyId, AuthUser user);
    Company update(Company company, AuthUser user);
    Optional<Company> findById(long companyId);
    Company findByIdOrThrow(long companyId);
    List<Company> findByStatus(ConfirmedStatus status);
}
