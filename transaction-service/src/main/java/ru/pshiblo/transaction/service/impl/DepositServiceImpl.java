package ru.pshiblo.transaction.service.impl;

import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Deposit;
import ru.pshiblo.transaction.service.DepositService;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public class DepositServiceImpl implements DepositService {
    @Override
    public Deposit create(int userId) {
        return null;
    }

    @Override
    public Deposit create(int userId, Account account) {
        return null;
    }

    @Override
    public List<Deposit> getByUserId(int userId) {
        return null;
    }

    @Override
    public Deposit getById(int id) {
        return null;
    }

    @Override
    public Deposit getByNumber(int number) {
        return null;
    }
}
