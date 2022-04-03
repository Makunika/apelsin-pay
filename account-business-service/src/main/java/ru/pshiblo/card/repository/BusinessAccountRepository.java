package ru.pshiblo.card.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.card.domain.BusinessAccount;

import java.util.List;
import java.util.Optional;

public interface BusinessAccountRepository extends JpaRepository<BusinessAccount, Integer> {
    Optional<BusinessAccount> findByAccount_Number(String number);
    List<BusinessAccount> findByCompanyId(Long companyId);
    Optional<BusinessAccount> findByAccount(Account account);

}