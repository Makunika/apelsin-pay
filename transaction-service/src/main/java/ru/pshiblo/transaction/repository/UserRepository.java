package ru.pshiblo.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.pshiblo.transaction.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
}