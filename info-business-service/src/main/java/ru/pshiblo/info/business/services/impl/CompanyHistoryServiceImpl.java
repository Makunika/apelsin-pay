package ru.pshiblo.info.business.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyHistory;
import ru.pshiblo.info.business.repository.CompanyHistoryRepository;
import ru.pshiblo.info.business.services.CompanyHistoryService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CompanyHistoryServiceImpl implements CompanyHistoryService {

    private final CompanyHistoryRepository repository;

    @Override
    public void create(Company company, String reason) {
        CompanyHistory history = new CompanyHistory();
        history.setCompany(company);
        history.setStatus(company.getStatus());
        history.setReason(reason);
        history.setInn(company.getInn());
        history.setName(company.getName());
        history.setAddress(company.getAddress());
        repository.save(history);
    }

    @Override
    public List<CompanyHistory> getByCompany(Company company) {
        return repository.findByCompany(company);
    }

    @Override
    public List<CompanyHistory> getByCompanyId(long companyId) {
        return repository.findByCompany_Id(companyId);
    }
}
