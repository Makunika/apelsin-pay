package ru.pshiblo.info.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByStatus(ConfirmedStatus status);
}