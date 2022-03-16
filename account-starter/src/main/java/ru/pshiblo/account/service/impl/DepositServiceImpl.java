package ru.pshiblo.account.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.service.DepositTypeService;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.domain.DepositType;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.repository.DepositRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.DepositService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final DepositTypeService depositTypeService;
    private final AccountService accountService;

    @Override
    public Deposit create(int userId, int depositTypeId) {
        DepositType depositType = depositTypeService.getById(depositTypeId)
                .orElseThrow(() -> new NotFoundException(depositTypeId, DepositType.class));
        return create(
                userId,
                accountService.create(userId, depositType.getCurrency(), AccountType.DEPOSIT),
                depositType
        );
    }

    @Override
    public Deposit create(int userId, Account account, DepositType depositType) {
        Deposit deposit = new Deposit();
        deposit.setUserId(userId);
        deposit.setDepositType(depositType);
        deposit.setNumber(account.getNumber());
        deposit.setAccount(account);
        if (depositType.getMinSum().compareTo(BigDecimal.ZERO) == 0) {
            deposit.setStartDepositDate(LocalDate.now());
        }
        return depositRepository.save(deposit);
    }

    @Override
    public List<Deposit> getByUserId(int userId) {
        return depositRepository.findByUserId(userId);
    }

    @Override
    public Optional<Deposit> getById(int id) {
        return depositRepository.findById(id);
    }

    @Override
    public Optional<Deposit> getByNumber(String number) {
        return depositRepository.findByNumber(number);
    }

    @Override
    public void block(String number, AuthUser authUser) {
        Deposit deposit = getByNumber(number).orElseThrow(() -> new NotFoundException(number, Deposit.class));

        if (!deposit.getUserId().equals((int) authUser.getId())) {
            throw new NotAllowedOperationException("Not allowed");
        }

        deposit.getAccount().setLock(true);
        depositRepository.save(deposit);
    }

    @Override
    public void blockById(int id) {
        Deposit deposit = getById(id).orElseThrow(() -> new NotFoundException(id, Deposit.class));
        deposit.getAccount().setLock(true);
        depositRepository.save(deposit);
    }

    @Override
    public void update(Deposit deposit) {
        depositRepository.save(deposit);
    }
}
