package ru.pshiblo.account.service;

import ru.pshiblo.account.domain.DepositType;

import java.util.List;
import java.util.Optional;

public interface DepositTypeService {
    DepositType createDepositType(DepositType depositType);
    Optional<DepositType> getById(int id);
    List<DepositType> getAll();
    void blockDepositType(int depositTypeId);
}
