package ru.pshiblo.transaction.service.interfaces;

import ru.pshiblo.transaction.domain.Deposit;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public interface DepositService {
    Deposit create(int userId);
    List<Deposit> getByUserId(int userId);
    Deposit getById(int id);
    Deposit getByNumber(int number);
}
