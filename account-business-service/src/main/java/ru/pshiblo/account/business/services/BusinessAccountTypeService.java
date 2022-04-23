package ru.pshiblo.account.business.services;

import ru.pshiblo.account.business.domain.BusinessAccountType;

import java.util.List;
import java.util.Optional;

public interface BusinessAccountTypeService {
    BusinessAccountType createType(BusinessAccountType depositType);
    Optional<BusinessAccountType> getById(int id);
    List<BusinessAccountType> getAll();
    void blockType(int typeId);
}
