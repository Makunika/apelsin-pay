package ru.pshiblo.info.personal.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;

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
    void ban(long id);
    void update(PersonInfo personInfo, long userId);

    Page<PersonInfo> findAll(Pageable pageable);
}
