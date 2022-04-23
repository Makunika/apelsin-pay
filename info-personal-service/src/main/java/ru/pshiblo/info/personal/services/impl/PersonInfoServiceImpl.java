package ru.pshiblo.info.personal.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.personal.clients.UsersClient;
import ru.pshiblo.info.personal.clients.model.RegisterAuthDto;
import ru.pshiblo.info.personal.clients.model.UserIdDto;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.enums.PersonStatus;
import ru.pshiblo.info.personal.repository.PersonInfoRepository;
import ru.pshiblo.info.personal.services.PersonInfoService;

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
        Assert.notNull(personInfo.getMiddleName(), "person m required not null");
        Assert.notNull(personInfo.getBirthday(), "person b required not null");
        Assert.notNull(personInfo.getEmail(), "person e required not null");
        Assert.notNull(personInfo.getPhone(), "person p required not null");

        personInfo.setPassportNumber(null);
        personInfo.setPassportSeries(null);
        personInfo.setId(null);
        personInfo.setStatus(PersonStatus.NOT_CONFIRMED);

        UserIdDto register = usersClient.register(
                RegisterAuthDto.builder()
                        .login(login)
                        .password(password)
                        .build());

        personInfo.setUserId(register.getId());

        repository.save(personInfo);
    }

    @Override
    public void confirmed(int passportNumber, int passportSeries, long personInfoId, long userId) {
        PersonInfo personInfo = findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));
        personInfo.setPassportSeries(passportSeries);
        personInfo.setPassportNumber(passportNumber);

        if (personInfo.getUserId() != userId) {
            throw new AccessDeniedException("userId not equals personInfoId");
        }

        if (personInfo.getStatus() == PersonStatus.ON_CONFIRMED || personInfo.getStatus() == PersonStatus.CONFIRMED) {
            throw new IllegalArgumentException("person already confirmed or on confirmed");
        }

        personInfo.setStatus(PersonStatus.ON_CONFIRMED);
        repository.save(personInfo);
    }

    @Override
    public void confirmedAccept(long personInfoId) {
        PersonInfo personInfo = findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));

        if (personInfo.getStatus() != PersonStatus.ON_CONFIRMED) {
            throw new IllegalArgumentException("person not in on confirmed");
        }

        personInfo.setStatus(PersonStatus.CONFIRMED);
        repository.save(personInfo);
    }

    @Override
    public void confirmedFailed(long personInfoId) {
        PersonInfo personInfo = findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));

        if (personInfo.getStatus() != PersonStatus.ON_CONFIRMED) {
            throw new IllegalArgumentException("person not in on confirmed");
        }

        personInfo.setStatus(PersonStatus.FAILED_CONFIRMED);
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
    public List<PersonInfo> findByOnConfirmed() {
        return repository.findByStatus(PersonStatus.ON_CONFIRMED);
    }
}
