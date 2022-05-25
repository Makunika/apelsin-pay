package ru.pshiblo.info.business.services;

import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.enums.RoleCompany;
import ru.pshiblo.security.model.AuthUser;

import java.util.List;

public interface CompanyUserService {
    List<CompanyUser> findUsersInCompany(Company company, AuthUser user);
    void addUserToCompany(Company company, AuthUser user, long userId, RoleCompany roleCompany);
    void addUserToCompany(Company company, long userId, RoleCompany roleCompany);
    void deleteUserFromCompany(Company company, AuthUser user, long userId);
    boolean isOwnerCompany(Company company, AuthUser user);
    boolean isOwnerOrModeratorCompany(Company company, AuthUser user);
    List<CompanyUser> findByUser(long userId);
}
