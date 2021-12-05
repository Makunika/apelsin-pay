package ru.pshiblo.transaction.service.interfaces;

import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.enums.Currency;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface AccountService {
    Account create(int userId, Currency currency);
    Account getById(int id);
    Account getByNumber(String number);
    List<Account> getByUserId(int userId);
    void save(Account account);
}
