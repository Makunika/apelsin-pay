package ru.pshiblo.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.domain.User;

public interface UserRepository extends JpaRepository<User, Integer> {
}