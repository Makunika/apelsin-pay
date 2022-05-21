package ru.pshiblo.account.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.account.domain.HoldMoney;
import ru.pshiblo.account.repository.HoldMoneyRepository;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.repository.AccountRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.utils.RandomGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final HoldMoneyRepository holdMoneyRepository;

    @Override
    public Account create(Currency currency, AccountType type, String ownerName) {
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
        if (number == null) {
            return Optional.empty();
        }
        return repository.findByNumber(number);
    }

    @Override
    @Transactional
    public void save(Account account) {
        repository.save(account);
    }
    @Override
    @Transactional
    public HoldMoney holdMoney(Account account, BigDecimal amount, LocalDateTime holdUntil) {
        BigDecimal balance = account.getBalance();
        BigDecimal holdSum = getCurrentHoldMoney(account);

        // FIXME: 04.05.2022 
        if (holdSum.add(amount).compareTo(balance) > 0) {
            throw new IllegalArgumentException("hold money > balance");
        }

        HoldMoney newHoldMoney = new HoldMoney();
        newHoldMoney.setHoldUntil(holdUntil);
        newHoldMoney.setAccount(account);
        newHoldMoney.setAmount(amount);
        return holdMoneyRepository.save(newHoldMoney);
    }

    @Override
    public BigDecimal getCurrentHoldMoney(Account account) {
        Set<HoldMoney> holdMoneys = holdMoneyRepository.findByAccountAndHoldUntilIsBefore(account, LocalDateTime.now());
        return holdMoneys.stream().map(HoldMoney::getAmount).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
    }

    @Override
    public void unHoldMoney(long holdMoneyId) {
        holdMoneyRepository.deleteById(holdMoneyId);
    }

}
