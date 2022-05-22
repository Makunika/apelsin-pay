package ru.pshiblo.info.business.services;

import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.enums.RoleCompany;
import ru.pshiblo.security.model.AuthUser;

import java.util.List;
import java.util.Optional;

public interface CompanyService {
    Company create(Company company, AuthUser user);
    void confirm(long companyId);
    void failedConfirm(long companyId, String reason);
    void delete(long companyId, AuthUser user);
    List<CompanyUser> findByUser(long userId);
    List<CompanyUser> findOwnerCompanies(long userId);
    List<CompanyUser> findUsersInCompany(long companyId, AuthUser user);
    Company update(Company company, AuthUser user);
    boolean isOwnerCompany(Company company, AuthUser user);
    boolean isOwnerCompany(long companyId, AuthUser user);
    Optional<Company> findById(long companyId);
    void addUserToCompany(long companyId, AuthUser user, long userId, RoleCompany roleCompany);
    void deleteUserFromCompany(long companyId, AuthUser user, long userId);
    boolean checkApiKey(long companyId, String apiKey);
    void regenerateApiKry(long companyId, AuthUser user);
    String getApiKey(long companyId, AuthUser user);
}
