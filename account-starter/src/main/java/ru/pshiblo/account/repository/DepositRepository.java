package ru.pshiblo.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.pshiblo.account.domain.Deposit;

import java.util.List;
import java.util.Optional;

public interface DepositRepository extends JpaRepository<Deposit, Integer> {
    Optional<Deposit> findByNumber(String number);
    List<Deposit> findByUserId(Integer userId);

    @Query(
            value = "SELECT * FROM deposits d " +
                    "JOIN accounts a ON a.id = d.account_id AND a.lock = false " +
                    "where extract(day from d.start_deposit_date) = :day and d.is_enabled = true",
            nativeQuery = true
    )
    List<Deposit> findByDayInStartDay(int day);
}