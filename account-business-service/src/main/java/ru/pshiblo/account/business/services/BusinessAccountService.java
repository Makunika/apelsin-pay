package ru.pshiblo.account.business.services;

import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.business.domain.BusinessAccount;
import ru.pshiblo.account.business.domain.BusinessAccountType;

import java.util.List;
import java.util.Optional;

public interface BusinessAccountService {
    BusinessAccount create(long companyId, long userId, BusinessAccountType type);
    Optional<BusinessAccount> getByAccount(Account account);
    Optional<BusinessAccount> getByNumber(String number);
    List<BusinessAccount> getByCompanyId(long companyId);
}
