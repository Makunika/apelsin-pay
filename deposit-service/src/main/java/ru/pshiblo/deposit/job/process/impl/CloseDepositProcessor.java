package ru.pshiblo.deposit.job.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.domain.DepositType;
import ru.pshiblo.account.service.DepositService;
import ru.pshiblo.deposit.job.process.AbstractDepositProcessor;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloseDepositProcessor extends AbstractDepositProcessor {

    private final DepositService depositService;

    @Override
    protected boolean hasSupportDepositType(DepositType depositType) {
        return true;
    }

    @Override
    protected void processDeposit(Deposit deposit) {
        if (checkToClose(deposit)) {
            deposit.setEnabled(false);
            depositService.update(deposit);
        }
    }

    private boolean checkToClose(Deposit deposit) {
        int months = deposit.getMonths();
        LocalDate startDepositDate = deposit.getStartDepositDate();
        LocalDate stopDepositDate = startDepositDate.plusMonths(months);
        LocalDate now = LocalDate.now();

        int maxMonths = Period.between(startDepositDate, stopDepositDate).getMonths();
        return Period.between(startDepositDate, now).getMonths() >= maxMonths;
    }
}
