package ru.pshiblo.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.domain.Deposit;

import java.util.List;
import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {
    Optional<Deposit> findByNumber(String number);
    List<Deposit> findByUserId(Integer userId);
}