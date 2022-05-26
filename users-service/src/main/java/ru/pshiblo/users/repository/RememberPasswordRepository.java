package ru.pshiblo.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.users.domain.RememberPassword;

import java.util.Optional;

public interface RememberPasswordRepository extends JpaRepository<RememberPassword, Long> {
    Optional<RememberPassword> findByToken(String token);
    boolean existsByToken(String token);
}