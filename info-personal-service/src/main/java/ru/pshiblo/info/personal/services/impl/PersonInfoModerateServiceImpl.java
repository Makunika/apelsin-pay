package ru.pshiblo.info.personal.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.AlreadyExistException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.enums.PersonStatus;
import ru.pshiblo.info.personal.services.PersonInfoModerateService;
import ru.pshiblo.info.personal.services.PersonInfoService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonInfoModerateServiceImpl implements PersonInfoModerateService {

    private final PersonInfoService service;

    @Override
    public List<PersonInfo> findByOnConfirmed() {
        return service.findByStatus(PersonStatus.ON_CONFIRMED);
    }

    @Override
    public void ban(long personInfoId) {
        PersonInfo personInfo = service.findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));

        personInfo.setIsLock(true);
        service.save(personInfo);
    }

    @Override
    public void unban(long personInfoId) {
        PersonInfo personInfo = service.findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));

        personInfo.setIsLock(false);
        service.save(personInfo);
    }

    @Override
    public void confirmed(String passportNumber, String passportSeries, long personInfoId, long userId) {
        PersonInfo personInfo = service.findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));
        personInfo.setPassportSeries(passportSeries);
        personInfo.setPassportNumber(passportNumber);

        if (service.existByPassport(passportSeries, passportNumber)) {
            throw new AlreadyExistException("Пользователь с такими паспортными данными уже существует");
        }

        if (personInfo.getUserId() != userId) {
            throw new AccessDeniedException("userId not equals personInfoId");
        }

        if (personInfo.getStatus() == PersonStatus.ON_CONFIRMED || personInfo.getStatus() == PersonStatus.CONFIRMED) {
            throw new IllegalArgumentException("person already confirmed or on confirmed");
        }

        personInfo.setStatus(PersonStatus.ON_CONFIRMED);
        service.save(personInfo);
    }

    @Override
    public void confirmedAccept(long personInfoId) {
        PersonInfo personInfo = service.findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));

        if (personInfo.getStatus() != PersonStatus.ON_CONFIRMED) {
            throw new IllegalArgumentException("person not in on confirmed");
        }

        personInfo.setStatus(PersonStatus.CONFIRMED);
        service.save(personInfo);
    }

    @Override
    public void confirmedFailed(long personInfoId) {
        PersonInfo personInfo = service.findById(personInfoId)
                .orElseThrow(() -> new NotFoundException(personInfoId, PersonInfo.class));

        if (personInfo.getStatus() != PersonStatus.ON_CONFIRMED) {
            throw new IllegalArgumentException("person not in on confirmed");
        }

        personInfo.setStatus(PersonStatus.FAILED_CONFIRMED);
        service.save(personInfo);
    }
}
