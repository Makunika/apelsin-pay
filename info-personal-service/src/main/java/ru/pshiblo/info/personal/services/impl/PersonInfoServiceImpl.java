package ru.pshiblo.info.personal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.pshiblo.common.exception.AlreadyExistException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.personal.clients.UsersClient;
import ru.pshiblo.info.personal.clients.model.RegisterAuthDto;
import ru.pshiblo.info.personal.clients.model.UserIdDto;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.enums.PersonStatus;
import ru.pshiblo.info.personal.repository.PersonInfoRepository;
import ru.pshiblo.info.personal.services.PersonInfoService;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonInfoServiceImpl implements PersonInfoService {

    private final PersonInfoRepository repository;
    private final UsersClient usersClient;

    @Override
    public void register(PersonInfo personInfo, String password, String login) {
        Assert.notNull(personInfo, "person required not null");
        Assert.notNull(personInfo.getFirstName(), "person f required not null");
        Assert.notNull(personInfo.getLastName(), "person l required not null");
        Assert.notNull(personInfo.getLogin(), "person l required not null");
        Assert.notNull(personInfo.getBirthday(), "person b required not null");
        Assert.notNull(personInfo.getEmail(), "person e required not null");
        Assert.notNull(personInfo.getPhone(), "person p required not null");

        personInfo.setPassportNumber(null);
        personInfo.setPassportSeries(null);
        personInfo.setId(null);
        personInfo.setStatus(PersonStatus.NOT_CONFIRMED);

        if (repository.existsByEmailOrPhone(personInfo.getEmail(), personInfo.getPhone())) {
            throw new AlreadyExistException("Пользователь с такой почтой и/или телефоном уже существует");
        }

        UserIdDto register = usersClient.register(
                RegisterAuthDto.builder()
                        .login(login)
                        .password(password)
                        .build());

        personInfo.setUserId(register.getId());

        repository.save(personInfo);
    }

    @Override
    public Optional<PersonInfo> findByUserId(long userId) {
        return repository.findByUserId(userId);
    }

    @Override
    public Optional<PersonInfo> findById(long personInfoId) {
        return repository.findById(personInfoId);
    }

    @Override
    public void update(PersonInfo personInfo, long userId) {
        Assert.notNull(personInfo, "person required not null");
        Assert.notNull(personInfo.getFirstName(), "person f required not null");
        Assert.notNull(personInfo.getLastName(), "person l required not null");
        Assert.notNull(personInfo.getBirthday(), "person b required not null");
        Assert.notNull(personInfo.getEmail(), "person e required not null");
        Assert.notNull(personInfo.getPhone(), "person p required not null");
        Assert.notNull(personInfo.getId(), "person id required not null");

        PersonInfo personInfoDb = findById(personInfo.getId())
                .orElseThrow(() -> new NotFoundException(personInfo.getId(), PersonInfo.class));

        if (personInfoDb.getUserId() != userId) {
            throw new AccessDeniedException("userId not equals personInfoId");
        }

        personInfoDb.setFirstName(personInfo.getFirstName());
        personInfoDb.setLastName(personInfo.getLastName());
        personInfoDb.setBirthday(personInfo.getBirthday());
        personInfoDb.setEmail(personInfo.getEmail());
        personInfoDb.setPhone(personInfo.getPhone());

        repository.save(personInfoDb);
    }

    @Override
    public Page<PersonInfo> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public List<PersonInfo> findAll() {
        return repository.findAll();
    }

    @Override
    public List<PersonInfo> findByLogin(String login) {
        return repository.findByLoginContainsIgnoreCase(login);
    }

    @Override
    public PersonInfo save(PersonInfo personInfo) {
        return repository.save(personInfo);
    }

    @Override
    public List<PersonInfo> findByStatus(PersonStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public boolean existByPassport(String passportSeries, String passportNumber) {
        return repository.existsByPassportSeriesAndPassportNumber(passportSeries, passportNumber);
    }
}
