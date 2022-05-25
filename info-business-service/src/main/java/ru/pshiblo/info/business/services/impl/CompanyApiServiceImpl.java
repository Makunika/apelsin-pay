package ru.pshiblo.info.business.services.impl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.repository.CompanyRepository;
import ru.pshiblo.info.business.services.CompanyApiService;
import ru.pshiblo.info.business.services.CompanyUserService;
import ru.pshiblo.security.enums.ConfirmedStatus;
import ru.pshiblo.security.model.AuthUser;

@Service
@RequiredArgsConstructor
public class CompanyApiServiceImpl implements CompanyApiService {

    private final CompanyRepository companyRepository;
    private final CompanyUserService companyUserService;

    @Override
    public boolean checkApiKey(Company company, String apiKey) {
        if (!company.getApiKey().equals(apiKey)) {
            throw new NotAllowedOperationException();
        }
        return true;
    }

    @Override
    public void regenerateApiKey(Company company, AuthUser user) {
        if (!companyUserService.isOwnerCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        company.setApiKey(RandomStringUtils.randomAlphanumeric(20));
        companyRepository.save(company);
    }

    @Override
    public String getApiKey(Company company, AuthUser user) {
        if (!companyUserService.isOwnerOrModeratorCompany(company, user)) {
            throw new NotAllowedOperationException();
        }
        if (company.getStatus() != ConfirmedStatus.CONFIRMED) {
            throw new NotAllowedOperationException("Company not confirmed");
        }
        return company.getApiKey();
    }
}
