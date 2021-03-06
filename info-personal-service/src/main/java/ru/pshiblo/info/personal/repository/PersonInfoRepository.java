package ru.pshiblo.info.personal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.enums.PersonStatus;

import java.util.List;
import java.util.Optional;

public interface PersonInfoRepository extends JpaRepository<PersonInfo, Long> {
    boolean existsByPassportSeriesAndPassportNumber(String passportSeries, String passportNumber);
    List<PersonInfo> findByLoginContainsIgnoreCase(String login);
    boolean existsByEmailOrPhone(String email, String phone);
    List<PersonInfo> findByStatus(PersonStatus status);
    Optional<PersonInfo> findByUserId(Long userId);
}