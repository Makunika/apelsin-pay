package ru.pshiblo.account.personal.core.services;

import ru.pshiblo.account.personal.core.domain.PersonalAccountType;

import java.util.List;
import java.util.Optional;

public interface PersonalAccountTypeService {
    PersonalAccountType createType(PersonalAccountType type);
    Optional<PersonalAccountType> getById(int id);
    List<PersonalAccountType> getAll();
    List<PersonalAccountType> getAllValid();
    void blockType(int typeId);
}
