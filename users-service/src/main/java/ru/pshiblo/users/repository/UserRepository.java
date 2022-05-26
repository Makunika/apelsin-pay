package ru.pshiblo.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.users.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
}