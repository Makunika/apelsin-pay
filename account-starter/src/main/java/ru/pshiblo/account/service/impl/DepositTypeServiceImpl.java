package ru.pshiblo.account.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.service.DepositTypeService;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.domain.DepositType;
import ru.pshiblo.account.repository.DepositTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DepositTypeServiceImpl implements DepositTypeService {

    private final DepositTypeRepository repository;

    @Override
    public DepositType createDepositType(DepositType depositType) {
        return repository.save(depositType);
    }

    @Override
    public Optional<DepositType> getById(int id) {
        return repository.findById(id);
    }

    @Override
    public List<DepositType> getAll() {
        return repository.findAll();
    }

    @Override
    public void blockDepositType(int depositTypeId) {
        DepositType depositType = getById(depositTypeId)
                .orElseThrow(() -> new NotFoundException(depositTypeId, DepositType.class));

        depositType.setValid(false);
        repository.save(depositType);
    }
}
