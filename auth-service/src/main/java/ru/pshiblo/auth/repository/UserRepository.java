package ru.pshiblo.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.auth.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByPassportNumberAndPassportSeries(Integer passportNumber, Integer passportSeries);
    boolean existsByEmail(String email);
}