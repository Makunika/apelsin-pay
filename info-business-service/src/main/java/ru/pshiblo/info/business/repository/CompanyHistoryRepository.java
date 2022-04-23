package ru.pshiblo.info.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyHistory;

import java.util.List;

public interface CompanyHistoryRepository extends JpaRepository<CompanyHistory, Long> {
    List<CompanyHistory> findByCompany_Id(Long id);

    List<CompanyHistory> findByCompany(Company company);
}