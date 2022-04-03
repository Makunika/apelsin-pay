package ru.pshiblo.deposit.core.services;

import ru.pshiblo.account.domain.*;
import ru.pshiblo.deposit.core.domain.PersonalAccount;
import ru.pshiblo.deposit.core.domain.PersonalAccountType;

import java.util.List;
import java.util.Optional;

public interface PersonalAccountService {
    PersonalAccount create(long userId, PersonalAccountType type);
    Optional<PersonalAccount> getByAccount(Account account);
    Optional<PersonalAccount> getByNumber(String number);
    List<PersonalAccount> getByUserId(long userId);
    List<PersonalAccount> getByStartWorkDay(int day);
    void update(PersonalAccount account);
}
