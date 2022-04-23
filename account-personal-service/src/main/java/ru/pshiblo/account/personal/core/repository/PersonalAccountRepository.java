package ru.pshiblo.account.personal.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;

import java.util.List;
import java.util.Optional;

public interface PersonalAccountRepository extends JpaRepository<PersonalAccount, Integer> {

    @Query(
            value = "SELECT * FROM personal_accounts d " +
                    "JOIN accounts a ON a.id = d.account_id AND a.lock = false " +
                    "where extract(day from d.start_work) = :day and d.is_enabled = true",
            nativeQuery = true
    )
    List<PersonalAccount> findByStartWorkDay(int day);
    Optional<PersonalAccount> findByAccount_Number(String number);
    Optional<PersonalAccount> findByAccount(Account account);
    List<PersonalAccount> findByUserId(Long userId);
}