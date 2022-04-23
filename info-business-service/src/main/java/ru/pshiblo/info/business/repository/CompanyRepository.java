package ru.pshiblo.info.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.info.business.domain.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}