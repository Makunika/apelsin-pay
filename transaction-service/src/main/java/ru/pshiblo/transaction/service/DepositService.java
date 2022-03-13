package ru.pshiblo.transaction.service;

import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.domain.Deposit;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface DepositService {
    Deposit create(int userId);
    Deposit create(int userId, Account account);
    List<Deposit> getByUserId(int userId);
    Deposit getById(int id);
    Deposit getByNumber(int number);
}
