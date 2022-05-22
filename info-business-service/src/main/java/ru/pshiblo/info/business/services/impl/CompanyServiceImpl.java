package ru.pshiblo.info.business.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.enums.RoleCompany;
import ru.pshiblo.info.business.repository.CompanyRepository;
import ru.pshiblo.info.business.repository.CompanyUserRepository;
import ru.pshiblo.info.business.services.CompanyHistoryService;
import ru.pshiblo.info.business.services.CompanyService;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.enums.ConfirmedStatus;
import ru.pshiblo.security.model.AuthUser;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyHistoryService historyService;

    @Override
    public Company create(Company company, AuthUser user) {
        Assert.notNull(company.getAddress(), "address");
        Assert.notNull(company.getInn(), "inn");
        Assert.notNull(company.getName(), "name");
        Assert.isNull(company.getId(), "id");

        company.setStatus(ConfirmedStatus.NOT_CONFIRMED);
        company.setApiKey(RandomStringUtils.randomAlphanumeric(20));
        company = companyRepository.save(company);
        CompanyUser companyUser = new CompanyUser();
        companyUser.setCompany(company);
        companyUser.setUserId(user.getId());
        companyUser.setRoleCompany(RoleCompany.OWNER);
        companyUserRepository.save(companyUser);
        log.info("Success saved company with name {} and owner {}", company.getName(), user.getName());
        historyService.create(company, "Created");
        return company;
    }

    @Override
    public void confirm(long companyId) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));

        if (ConfirmedStatus.NOT_CONFIRMED != company.getStatus()) {
            throw new IllegalArgumentException("Status not in NOT CONFIRMED");
        }

        company.setStatus(ConfirmedStatus.CONFIRMED);
        company = companyRepository.save(company);
        historyService.create(company, "confirmed");
    }

    @Override
    public void failedConfirm(long companyId, String reason) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));

        if (ConfirmedStatus.NOT_CONFIRMED != company.getStatus()) {
            throw new IllegalArgumentException("Status not in NOT CONFIRMED");
        }

        company.setStatus(ConfirmedStatus.FAILED_CONFIRMED);
        company = companyRepository.save(company);
        historyService.create(company, reason);
    }

    @Override
    public void delete(long companyId, AuthUser user) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));
        if (!isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        company.setIsDeleted(true);
        companyRepository.save(company);
    }

    @Override
    public List<CompanyUser> findByUser(long userId) {
        return companyUserRepository.findByUserId(userId);
    }

    @Override
    public List<CompanyUser> findOwnerCompanies(long userId) {
        return companyUserRepository.findByUserIdAndRoleCompany(userId, RoleCompany.OWNER);
    }
    @Override
    public List<CompanyUser> findUsersInCompany(long companyId, AuthUser user) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));
        if (!isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        return companyUserRepository.findByCompany_Id(companyId);
    }

    @Override
    public Company update(Company company, AuthUser user) {
        Assert.notNull(company.getAddress(), "address");
        Assert.notNull(company.getInn(), "inn");
        Assert.notNull(company.getName(), "name");
        Assert.notNull(company.getId(), "id");

        Company companyInDb = findById(company.getId()).orElseThrow(() -> new NotFoundException(company.getId(), Company.class));
        if (!isOwnerCompany(companyInDb, user) && companyInDb.getStatus() == ConfirmedStatus.CONFIRMED) {
            throw new NotAllowedOperationException();
        }

        companyInDb.setAddress(company.getAddress());
        companyInDb.setInn(company.getInn());
        companyInDb.setAddress(company.getAddress());
        companyInDb.setName(company.getName());
        companyInDb.setStatus(ConfirmedStatus.ON_CONFIRMED);

        companyInDb = companyRepository.save(companyInDb);
        log.info("Success update company with name {} and owner {}", company.getName(), user.getName());
        historyService.create(companyInDb, "Updated");
        return companyInDb;
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
    public boolean isOwnerCompany(long companyId, AuthUser user) {
        return isOwnerCompany(
                findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class)),
                user
        );
    }

    @Override
    public Optional<Company> findById(long companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public void addUserToCompany(long companyId, AuthUser user, long userId, RoleCompany roleCompany) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));
        if (!isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
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
    public void deleteUserFromCompany(long companyId, AuthUser user, long userId) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));
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
    public boolean checkApiKey(long companyId, String apiKey) {
        Company company = findById(companyId)
                .orElseThrow(() -> new NotFoundException(companyId, Company.class));
        if (!company.getApiKey().equals(apiKey)) {
            throw new NotAllowedOperationException();
        }
        return true;
    }

    @Override
    public void regenerateApiKry(long companyId, AuthUser user) {
        Company company = findById(companyId)
                .orElseThrow(() -> new NotFoundException(companyId, Company.class));

        if (!isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }

        company.setApiKey(RandomStringUtils.randomAlphanumeric(20));
        companyRepository.save(company);
    }

    @Override
    public String getApiKey(long companyId, AuthUser user) {
        Company company = findById(companyId)
                .orElseThrow(() -> new NotFoundException(companyId, Company.class));

        if (!isOwnerCompany(companyId, user)) {
            throw new NotAllowedOperationException();
        }

        if (company.getStatus() != ConfirmedStatus.CONFIRMED) {
            throw new NotAllowedOperationException("Company not confirmed");
        }

        return company.getApiKey();
    }
}
