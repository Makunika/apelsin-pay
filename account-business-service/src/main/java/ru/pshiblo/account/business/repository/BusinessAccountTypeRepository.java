package ru.pshiblo.account.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.business.domain.BusinessAccountType;

public interface BusinessAccountTypeRepository extends JpaRepository<BusinessAccountType, Integer> {
}