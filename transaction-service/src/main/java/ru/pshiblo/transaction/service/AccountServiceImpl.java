package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.transaction.domain.Account;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.feign.AuthServiceClient;
import ru.pshiblo.transaction.repository.AccountRepository;
import ru.pshiblo.transaction.service.interfaces.AccountService;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository repository;
    private final AuthServiceClient authServiceClient;

    @Override
    public Account create(int userId) {
        Account account = new Account();
        account.setCurrency(Currency.RUB);;
        return null;
    }

    @Override
    public Account getById(int id) {
        return null;
    }

    @Override
    public List<Account> getByUserId(int userId) {
        return null;
    }
}
