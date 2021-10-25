package ru.pshiblo.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.domain.UserPassword;

import java.util.Optional;

public interface UserPasswordRepository extends JpaRepository<UserPassword, String> {
    Optional<UserPassword> findByUser(User user);
    boolean existsByLogin(String login);
    Optional<UserPassword> findByLogin(String login);
}