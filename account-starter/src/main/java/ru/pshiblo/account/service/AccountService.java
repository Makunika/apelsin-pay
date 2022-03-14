package ru.pshiblo.account.service;

import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface AccountService {
    Account create(int userId, Currency currency, AccountType type);
    Account getById(int id);
    Account getByNumber(String number);
    List<Account> getByUserId(int userId);
    void save(Account account);
}