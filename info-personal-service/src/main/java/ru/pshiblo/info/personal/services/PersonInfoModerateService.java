package ru.pshiblo.info.personal.services;

import ru.pshiblo.info.personal.domain.PersonInfo;

import java.util.List;

public interface PersonInfoModerateService {
    List<PersonInfo> findByOnConfirmed();
    void ban(long id);
    void unban(long id);
    void confirmed(String passportNumber, String passportSeries, long personInfoId, long userId);
    void confirmedAccept(long personInfoId);
    void confirmedFailed(long personInfoId);
}
