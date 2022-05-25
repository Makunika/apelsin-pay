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
import ru.pshiblo.info.business.services.CompanyUserService;
import ru.pshiblo.info.business.web.dto.CompanyResponseDto;
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
    private final CompanyHistoryService historyService;
    private final CompanyUserService companyUserService;

    @Override
    public Company create(Company company, AuthUser user) {
        Assert.notNull(company.getAddress(), "address");
        Assert.notNull(company.getInn(), "inn");
        Assert.notNull(company.getName(), "name");
        Assert.isNull(company.getId(), "id");

        company.setStatus(ConfirmedStatus.ON_CONFIRMED);
        company.setApiKey(RandomStringUtils.randomAlphanumeric(20));
        company = companyRepository.save(company);
        companyUserService.addUserToCompany(company, user.getId(), RoleCompany.OWNER);
        log.info("Success saved company with name {} and owner {}", company.getName(), user.getName());
        historyService.create(company, "Created");
        return company;
    }

    @Override
    public void delete(long companyId, AuthUser user) {
        Company company = findById(companyId).orElseThrow(() -> new NotFoundException(companyId, Company.class));
        if (!companyUserService.isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        company.setIsDeleted(true);
        companyRepository.save(company);
    }

    @Override
    public Company update(Company company, AuthUser user) {
        Assert.notNull(company.getAddress(), "address");
        Assert.notNull(company.getInn(), "inn");
        Assert.notNull(company.getName(), "name");
        Assert.notNull(company.getId(), "id");

        Company companyInDb = findById(company.getId()).orElseThrow(() -> new NotFoundException(company.getId(), Company.class));
        if (!companyUserService.isOwnerCompany(companyInDb, user) || companyInDb.getStatus() == ConfirmedStatus.CONFIRMED) {
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
    public Optional<Company> findById(long companyId) {
        return companyRepository.findById(companyId);
    }

    @Override
    public Company findByIdOrThrow(long companyId) {
        return findById(companyId).orElseThrow(() -> new NotFoundException(companyId, "Компания"));
    }

    @Override
    public List<Company> findByStatus(ConfirmedStatus status) {
        return companyRepository.findByStatus(status);
    }
}
