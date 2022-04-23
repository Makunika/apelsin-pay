package ru.pshiblo.info.business.services;

import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyHistory;

import java.util.List;

public interface CompanyHistoryService {
    void create(Company company, String reason);
    List<CompanyHistory> getByCompany(Company company);
    List<CompanyHistory> getByCompanyId(long companyId);
}
