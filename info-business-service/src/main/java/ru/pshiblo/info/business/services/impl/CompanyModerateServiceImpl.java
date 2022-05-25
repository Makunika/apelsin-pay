package ru.pshiblo.info.business.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.repository.CompanyRepository;
import ru.pshiblo.info.business.services.CompanyHistoryService;
import ru.pshiblo.info.business.services.CompanyModerateService;
import ru.pshiblo.security.enums.ConfirmedStatus;

@Service
@RequiredArgsConstructor
public class CompanyModerateServiceImpl implements CompanyModerateService {

    private final CompanyRepository companyRepository;
    private final CompanyHistoryService historyService;

    @Override
    public void acceptConfirm(Company company) {

        if (ConfirmedStatus.ON_CONFIRMED != company.getStatus()) {
            throw new IllegalArgumentException("Status not in ON CONFIRMED");
        }

        company.setStatus(ConfirmedStatus.CONFIRMED);
        company = companyRepository.save(company);
        historyService.create(company, "Confirmed");
    }

    @Override
    public void failedConfirm(Company company, String reason) {

        if (ConfirmedStatus.ON_CONFIRMED != company.getStatus()) {
            throw new IllegalArgumentException("Status not in ON CONFIRMED");
        }

        company.setStatus(ConfirmedStatus.FAILED_CONFIRMED);
        company = companyRepository.save(company);
        historyService.create(company, reason);
    }
}
