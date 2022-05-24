package ru.pshiblo.info.personal.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.enums.PersonStatus;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.util.List;
import java.util.Optional;

public interface PersonInfoService {
    void register(PersonInfo personInfo, String password, String login);
    Optional<PersonInfo> findByUserId(long userId);
    Optional<PersonInfo> findById(long personInfoId);
    void update(PersonInfo personInfo, long userId);
    Page<PersonInfo> findAll(Pageable pageable);
    List<PersonInfo> findAll();
    List<PersonInfo> findByLogin(String login);
    PersonInfo save(PersonInfo personInfo);
    List<PersonInfo> findByStatus(PersonStatus status);
    boolean existByPassport(String passportSeries, String passportNumber);
}
