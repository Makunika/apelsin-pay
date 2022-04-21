package ru.pshiblo.info.personal.services;

import org.springframework.security.access.prepost.PreAuthorize;
import ru.pshiblo.info.personal.domain.PersonInfo;

import java.util.List;
import java.util.Optional;

public interface PersonInfoService {
    void register(PersonInfo personInfo, String password, String login);
    void confirmed(int passportNumber, int passportSeries, long personInfoId, long userId);
    void confirmedAccept(long personInfoId);
    void confirmedFailed(long personInfoId);
    Optional<PersonInfo> findByUserId(long userId);
    Optional<PersonInfo> findById(long personInfoId);
    List<PersonInfo> findByOnConfirmed();
}
