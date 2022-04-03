package ru.pshiblo.card.services;

import ru.pshiblo.card.domain.BusinessAccountType;
import ru.pshiblo.account.domain.DepositType;

import java.util.List;
import java.util.Optional;

public interface BusinessAccountTypeService {
    BusinessAccountType createType(BusinessAccountType depositType);
    Optional<BusinessAccountType> getById(int id);
    List<BusinessAccountType> getAll();
    void blockType(int typeId);
}
