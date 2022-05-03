package ru.pshiblo.account.business.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.business.clients.InfoBusinessClient;
import ru.pshiblo.account.business.clients.model.CompanyUser;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.business.domain.BusinessAccount;
import ru.pshiblo.account.business.domain.BusinessAccountType;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.business.repository.BusinessAccountRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.business.services.BusinessAccountService;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessAccountServiceImpl implements BusinessAccountService {

    private final AccountService accountService;
    private final BusinessAccountRepository repository;
    private final InfoBusinessClient infoBusinessClient;

    @Override
    public BusinessAccount create(long companyId, long userId, BusinessAccountType type) {
        if (infoBusinessClient.findByUser(userId)
                .stream()
                .filter(cu -> cu.getCompany().getId().equals(companyId))
                .filter(cu -> cu.getCompany().getStatus() == ConfirmedStatus.CONFIRMED)
                .noneMatch(cu -> "OWNER".equals(cu.getRoleCompany()))) {
            throw new NotAllowedOperationException();
        }

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

    @Override
    public boolean checkOwnerBusinessAccount(long userId, String number) {
        BusinessAccount businessAccount = repository.findByAccount_Number(number).orElse(null);
        if (businessAccount == null) {
            return false;
        }
        List<CompanyUser> companyUsers = infoBusinessClient.findByUser(userId);
        return companyUsers
                .stream()
                .filter(cu -> cu.getCompany().getStatus() == ConfirmedStatus.CONFIRMED)
                .filter(cu -> cu.getCompany().getId().equals(businessAccount.getCompanyId()))
                .anyMatch(cu -> "OWNER".equals(cu.getRoleCompany()));
    }
}
