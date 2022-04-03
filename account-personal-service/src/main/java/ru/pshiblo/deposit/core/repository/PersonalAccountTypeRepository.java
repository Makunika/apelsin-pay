package ru.pshiblo.deposit.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.deposit.core.domain.PersonalAccountType;

public interface PersonalAccountTypeRepository extends JpaRepository<PersonalAccountType, Integer> {
}