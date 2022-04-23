package ru.pshiblo.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.users.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
}