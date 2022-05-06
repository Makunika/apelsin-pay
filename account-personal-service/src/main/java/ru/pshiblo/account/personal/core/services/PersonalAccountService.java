package ru.pshiblo.account.personal.core.services;

import ru.pshiblo.account.domain.*;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;
import ru.pshiblo.security.model.AuthUser;

import java.util.List;
import java.util.Optional;

public interface PersonalAccountService {
    PersonalAccount create(AuthUser user, PersonalAccountType type);
    Optional<PersonalAccount> getByAccount(Account account);
    Optional<PersonalAccount> getByNumber(String number);
    List<PersonalAccount> getByUserId(long userId);
    List<PersonalAccount> getByStartWorkDay(int day);
    void update(PersonalAccount account);
    boolean checkOwnerPersonalAccount(long userId, String number);
}
