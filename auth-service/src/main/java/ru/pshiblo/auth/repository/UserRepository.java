package ru.pshiblo.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.auth.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByLogin(String login);
}