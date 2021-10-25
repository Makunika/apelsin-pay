package ru.pshiblo.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.auth.domain.Token;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {
    long deleteByUser_Id(Integer id);
    Optional<Token> findByToken(String token);
}