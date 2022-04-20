package ru.pshiblo.account.business.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.business.domain.BusinessAccount;
import ru.pshiblo.account.business.domain.BusinessAccountType;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.business.repository.BusinessAccountRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.business.services.BusinessAccountService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessAccountServiceImpl implements BusinessAccountService {

    private final AccountService accountService;
    private final BusinessAccountRepository repository;

    @Override
    public BusinessAccount create(long companyId, long userId, BusinessAccountType type) {
        //TODO: check com[any id and user iD
        Account account = accountService.create(type.getCurrency(), AccountType.BUSINESS);
        BusinessAccount businessAccount = new BusinessAccount();
        businessAccount.setAccount(account);
        businessAccount.setType(type);
        businessAccount.setCompanyId(companyId);
        return repository.save(businessAccount);
    }

    @Override
    public Optional<BusinessAccount> getByAccount(Account account) {
        return repository.findByAccount(account);
    }

    @Override
    public Optional<BusinessAccount> getByNumber(String number) {
        return repository.findByAccount_Number(number);
    }

    @Override
    public List<BusinessAccount> getByCompanyId(long companyId) {
        return repository.findByCompanyId(companyId);
    }
}
