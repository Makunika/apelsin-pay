package ru.pshiblo.account.service;

import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.HoldMoney;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
public interface AccountService {
    Account create(Currency currency, AccountType type);
    Account getById(int id);
    Account getByNumber(String number);
    Optional<Account> findByNumber(String number);
    void save(Account account);
    HoldMoney holdMoney(Account account, BigDecimal amount, LocalDateTime holdUntil);
    BigDecimal getCurrentHoldMoney(Account account);
    void unHoldMoney(long holdMoneyId);
}
