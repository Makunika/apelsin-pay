package ru.pshiblo.info.business.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.enums.RoleCompany;
import ru.pshiblo.info.business.repository.CompanyUserRepository;
import ru.pshiblo.info.business.services.CompanyUserService;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.model.AuthUser;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

    private final CompanyUserRepository companyUserRepository;

    @Override
    public List<CompanyUser> findUsersInCompany(Company company, AuthUser user) {
        if (!isOwnerOrModeratorCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        return companyUserRepository.findByCompany(company);
    }

    @Override
    public List<CompanyUser> findByUser(long userId) {
        return companyUserRepository.findByUserId(userId);
    }

    @Override
    public void addUserToCompany(Company company, AuthUser user, long userId, RoleCompany roleCompany) {
        if (!isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        addUserToCompany(company, userId, roleCompany);
    }

    @Override
    public void addUserToCompany(Company company, long userId, RoleCompany roleCompany) {
        CompanyUser companyUser = company.getCompanyUsers()
                .stream()
                .filter(cu -> cu.getUserId() == userId)
                .findFirst()
                .orElseGet(() -> {
                    CompanyUser cu = new CompanyUser();
                    cu.setUserId(userId);
                    cu.setCompany(company);
                    return cu;
                });
        companyUser.setRoleCompany(roleCompany);
        companyUserRepository.save(companyUser);
    }

    @Override
    public void deleteUserFromCompany(Company company, AuthUser user, long userId) {
        if (!isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        company.getCompanyUsers()
                .stream()
                .filter(cu -> cu.getUserId() == userId)
                .findFirst()
                .ifPresent(companyUserRepository::delete);
    }
    @Override
    public boolean isOwnerCompany(Company company, AuthUser user) {
        Set<CompanyUser> companyUsers = company.getCompanyUsers();
        return companyUsers
                .stream()
                .anyMatch(cu -> cu.getUserId() == user.getId() && cu.getRoleCompany() == RoleCompany.OWNER)
                ||
                AuthUtils.hasRole(AuthUtils.ROLE_ADMINISTRATOR)
                ||
                AuthUtils.getAuthUser().isServer()
                ;
    }

    @Override
    public boolean isOwnerOrModeratorCompany(Company company, AuthUser user) {
        Set<CompanyUser> companyUsers = company.getCompanyUsers();
        return companyUsers
                .stream()
                .anyMatch(cu -> cu.getUserId() == user.getId())
                ||
                AuthUtils.hasRole(AuthUtils.ROLE_ADMINISTRATOR)
                ||
                AuthUtils.getAuthUser().isServer()
                ;
    }
}
