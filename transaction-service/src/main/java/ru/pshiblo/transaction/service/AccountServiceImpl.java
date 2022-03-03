package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.repository.AccountRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;
import ru.pshiblo.transaction.utils.RandomGenerator;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;

    @Override
    public Account create(int userId, Currency currency) {
        Account account = new Account();
        account.setCurrency(currency);
        account.setBalance(new BigDecimal(0));
        account.setLock(false);
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
    public List<Account> getByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    @Override
    @Transactional
    public void save(Account account) {
        repository.save(account);
    }
}
