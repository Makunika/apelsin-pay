package ru.pshiblo.card.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.card.domain.BusinessAccountType;
import ru.pshiblo.card.repository.BusinessAccountTypeRepository;
import ru.pshiblo.card.services.BusinessAccountTypeService;
import ru.pshiblo.common.exception.NotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BusinessAccountTypeServiceImpl implements BusinessAccountTypeService {

    private final BusinessAccountTypeRepository repository;

    @Override
    public BusinessAccountType createType(BusinessAccountType type) {
        return repository.save(type);
    }

    @Override
    public Optional<BusinessAccountType> getById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<BusinessAccountType> getAll() {
        return repository.findAll();
    }

    @Override
    public void blockType(int typeId) {
        BusinessAccountType businessAccountType = getById(typeId).orElseThrow(() -> new NotFoundException(typeId, BusinessAccountType.class));
        businessAccountType.setValid(false);
        repository.save(businessAccountType);
    }
}
