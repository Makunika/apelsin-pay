package ru.pshiblo.deposit.job.process;

import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.domain.DepositType;

public abstract class AbstractDepositProcessor implements DepositProcessor {

    protected abstract boolean hasSupportDepositType(DepositType depositType);
    protected abstract void processDeposit(Deposit deposit);

    @Override
    public void process(Deposit deposit) {
        if (hasSupportDepositType(deposit.getDepositType()) && deposit.isEnabled()) {
            processDeposit(deposit);
        }
    }
}
