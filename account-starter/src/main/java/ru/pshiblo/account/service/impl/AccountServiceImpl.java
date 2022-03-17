package ru.pshiblo.account.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.repository.AccountRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.utils.RandomGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public Account create(int userId, Currency currency, AccountType type) {
        Account account = new Account();
        account.setCurrency(currency);
        account.setBalance(new BigDecimal(0));
        account.setLock(false);
        account.setType(type);
        String number;
        do {
            number = RandomGenerator.randomNumber(20);
        } while (repository.existsByNumber(number));
        account.setNumber(number);
        account.setUserId(userId);
        return repository.save(account);
    }

    @Override
    public Account getById(int id) {
        return repository.findById(id).orElseThrow(() -> new NotFoundException(id, Account.class));
    }

    @Override
    public Account getByNumber(String number) {
        return repository.findByNumber(number).orElseThrow(() -> new NotFoundException("Account with number " + number + " not found"));
    }

    @Override
    public Optional<Account> findByNumber(String number) {
        return repository.findByNumber(number);
    }

    @Override
    public List<Account> getByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void save(Account account) {
        repository.save(account);
    }
}
