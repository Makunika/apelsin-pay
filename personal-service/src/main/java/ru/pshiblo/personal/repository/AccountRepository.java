package ru.pshiblo.personal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.personal.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}