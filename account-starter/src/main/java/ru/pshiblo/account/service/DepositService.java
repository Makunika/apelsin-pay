package ru.pshiblo.account.service;

import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.domain.DepositType;

import java.util.List;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
public interface DepositService {
    Deposit create(int userId, int depositTypeId);
    Deposit create(int userId, Account account, DepositType depositType);
    List<Deposit> getByUserId(int userId);
    Optional<Deposit> getById(int id);
    Optional<Deposit> getByNumber(String number);
    void block(String number, AuthUser authUser);
    void blockById(int id);
    void update(Deposit deposit);
}
