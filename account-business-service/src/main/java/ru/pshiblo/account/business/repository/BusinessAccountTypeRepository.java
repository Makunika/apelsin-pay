package ru.pshiblo.account.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.business.domain.BusinessAccountType;

import java.util.List;

public interface BusinessAccountTypeRepository extends JpaRepository<BusinessAccountType, Integer> {
    List<BusinessAccountType> findByIsValidIsTrue();

}