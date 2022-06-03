package ru.pshiblo.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.HoldMoney;

import java.time.LocalDateTime;
import java.util.Set;

public interface HoldMoneyRepository extends JpaRepository<HoldMoney, Long> {
    Set<HoldMoney> findByAccountAndHoldUntilIsAfter(Account account, LocalDateTime holdUntil);

}