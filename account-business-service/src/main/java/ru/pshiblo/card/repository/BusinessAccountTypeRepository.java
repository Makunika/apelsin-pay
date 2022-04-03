package ru.pshiblo.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.card.domain.BusinessAccountType;

public interface BusinessAccountTypeRepository extends JpaRepository<BusinessAccountType, Integer> {
}