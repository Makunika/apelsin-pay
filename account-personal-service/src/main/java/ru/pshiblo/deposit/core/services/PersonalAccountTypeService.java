package ru.pshiblo.deposit.core.services;

import ru.pshiblo.deposit.core.domain.PersonalAccountType;

import java.util.List;
import java.util.Optional;

public interface PersonalAccountTypeService {
    PersonalAccountType createType(PersonalAccountType type);
    Optional<PersonalAccountType> getById(int id);
    List<PersonalAccountType> getAll();
    void blockType(int typeId);
}
