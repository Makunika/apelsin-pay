package ru.pshiblo.transaction.service.interfaces;

import ru.pshiblo.transaction.domain.Account;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface AccountService {
    Account create(int userId);
    Account getById(int id);
    List<Account> getByUserId(int userId);
}
