package ru.pshiblo.account.personal.core.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;
import ru.pshiblo.account.personal.core.repository.PersonalAccountRepository;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.personal.core.services.PersonalAccountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalAccountServiceImpl implements PersonalAccountService {

    private final AccountService accountService;
    private final PersonalAccountRepository repository;

    @Override
    public PersonalAccount create(long userId, PersonalAccountType type) {
        //TODO: check limit and some!
        Account account = accountService.create(type.getCurrency(), AccountType.BUSINESS);
        PersonalAccount personalAccount = new PersonalAccount();
        personalAccount.setAccount(account);
        personalAccount.setType(type);
        personalAccount.setUserId(userId);

        if (!type.isRequiredToFirstPay()) {
            personalAccount.setIsEnabled(true);
            personalAccount.setStartWork(LocalDateTime.now());
        }

        return repository.save(personalAccount);
    }

    @Override
    public Optional<PersonalAccount> getByAccount(Account account) {
        return repository.findByAccount(account);
    }

    @Override
    public Optional<PersonalAccount> getByNumber(String number) {
        return repository.findByAccount_Number(number);
    }

    @Override
    public List<PersonalAccount> getByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public List<PersonalAccount> getByStartWorkDay(int day) {
        return repository.findByStartWorkDay(day);
    }

    @Override
    public void update(PersonalAccount account) {
        repository.save(account);
    }

    @Override
    public boolean checkOwnerPersonalAccount(long userId, String number) {
        PersonalAccount personalAccount = getByNumber(number).orElse(null);
        if (personalAccount == null) {
            return false;
        }
        return personalAccount.getUserId() == userId;
    }
}
