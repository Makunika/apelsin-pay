package ru.pshiblo.deposit.job.process;

import ru.pshiblo.account.domain.Deposit;

public interface DepositProcessor {
    void process(Deposit deposit);
}
