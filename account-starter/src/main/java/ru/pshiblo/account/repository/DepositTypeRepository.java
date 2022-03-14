package ru.pshiblo.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.domain.DepositType;

public interface DepositTypeRepository extends JpaRepository<DepositType, Integer> {
}