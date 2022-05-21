package ru.pshiblo.account.business.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.account.business.clients.InfoBusinessClient;
import ru.pshiblo.account.business.clients.model.CompanyUser;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.business.domain.BusinessAccount;
import ru.pshiblo.account.business.domain.BusinessAccountType;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.business.repository.BusinessAccountRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.business.services.BusinessAccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.common.exception.AlreadyExistException;
import ru.pshiblo.common.exception.ApelsinException;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessAccountServiceImpl implements BusinessAccountService {

    private final AccountService accountService;
    private final BusinessAccountRepository repository;
    private final InfoBusinessClient infoBusinessClient;
    private final CurrencyService currencyService;

    @Override
    public BusinessAccount create(long companyId, long userId, BusinessAccountType type) {

        if (repository.findByCompanyId(companyId).isPresent()) {
            throw new AlreadyExistException("У данной компании уже есть счет");
        }

        CompanyUser companyUser = infoBusinessClient.findByUser(userId)
                .stream()
                .filter(cu -> cu.getCompany().getId().equals(companyId))
                .filter(cu -> cu.getCompany().getStatus() == ConfirmedStatus.CONFIRMED)
                .filter(cu -> "OWNER".equals(cu.getRoleCompany()))
                .findFirst()
                .orElseThrow(NotAllowedOperationException::new);

        Account account = accountService.create(type.getCurrency(), AccountType.BUSINESS, companyUser.getCompany().getName());
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
    public BusinessAccount getByCompanyId(long companyId) {
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

    @Override
    @Transactional
    public void changeTypeOfAccount(BusinessAccountType type, String number, long userId) {
        if (!checkOwnerBusinessAccount(userId, number)) {
            throw new NotAllowedOperationException();
        }

        BusinessAccount businessAccount = repository.findByAccount_Number(number)
                .orElseThrow(() -> new NotFoundException(number, "Счет"));

        if (businessAccount.getType().equals(type)) {
            return;
        }

        if (businessAccount.getType().getCurrency() == type.getCurrency()) {
            businessAccount.setType(type);
            repository.save(businessAccount);
        } else {
            businessAccount.setType(type);
            repository.save(businessAccount);
            BigDecimal convertMoney = currencyService.convertMoney(
                    businessAccount.getType().getCurrency(),
                    type.getCurrency(),
                    businessAccount.getAccount().getBalance()
            );
            Account account = businessAccount.getAccount();
            account.setBalance(convertMoney);
            account.setCurrency(type.getCurrency());
            accountService.save(account);
        }
    }
}
