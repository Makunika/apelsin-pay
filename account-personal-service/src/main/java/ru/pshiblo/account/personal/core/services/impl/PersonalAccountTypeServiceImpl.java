package ru.pshiblo.account.personal.core.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;
import ru.pshiblo.account.personal.core.repository.PersonalAccountTypeRepository;
import ru.pshiblo.account.personal.core.services.PersonalAccountTypeService;
import ru.pshiblo.common.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalAccountTypeServiceImpl implements PersonalAccountTypeService {

    private final PersonalAccountTypeRepository repository;

    @Override
    public PersonalAccountType createType(PersonalAccountType type) {
        type.setValid(true);
        return repository.save(type);
    }

    @Override
    public Optional<PersonalAccountType> getById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<PersonalAccountType> getAll() {
        return repository.findAll();
    }

    @Override
    public List<PersonalAccountType> getAllValid() {
        return repository.findByIsValidTrue();
    }

    @Override
    public void blockType(int typeId) {
        PersonalAccountType personalAccountType = getById(typeId).orElseThrow(() -> new NotFoundException(typeId, PersonalAccountType.class));
        personalAccountType.setValid(false);
        repository.save(personalAccountType);
    }
}
