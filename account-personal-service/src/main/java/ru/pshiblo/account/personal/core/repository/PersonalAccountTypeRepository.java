package ru.pshiblo.account.personal.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;

import java.util.List;

public interface PersonalAccountTypeRepository extends JpaRepository<PersonalAccountType, Integer> {
    List<PersonalAccountType> findByIsValidTrue();
}