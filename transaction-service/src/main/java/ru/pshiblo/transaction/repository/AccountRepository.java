package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.transaction.domain.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    boolean existsByNumber(String number);
    List<Account> findByUserId(int userId);
    Optional<Account> findByNumber(String number);
}