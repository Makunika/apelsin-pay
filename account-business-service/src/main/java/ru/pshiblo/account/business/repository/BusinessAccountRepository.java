package ru.pshiblo.account.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.business.domain.BusinessAccount;

import java.util.List;
import java.util.Optional;

public interface BusinessAccountRepository extends JpaRepository<BusinessAccount, Integer> {
    Optional<BusinessAccount> findByAccount_Number(String number);
    Optional<BusinessAccount> findByCompanyId(Long companyId);
    Optional<BusinessAccount> findByAccount(Account account);

}