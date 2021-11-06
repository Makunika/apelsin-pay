package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.transaction.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}