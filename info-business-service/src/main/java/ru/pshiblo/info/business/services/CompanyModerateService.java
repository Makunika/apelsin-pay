package ru.pshiblo.info.business.services;

import ru.pshiblo.info.business.domain.Company;

public interface CompanyModerateService {
    void acceptConfirm(Company company);
    void failedConfirm(Company company, String reason);
}
