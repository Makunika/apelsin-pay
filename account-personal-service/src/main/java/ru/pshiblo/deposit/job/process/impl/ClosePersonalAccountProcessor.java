package ru.pshiblo.deposit.job.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.deposit.core.domain.PersonalAccount;
import ru.pshiblo.deposit.core.domain.PersonalAccountType;
import ru.pshiblo.deposit.job.process.AbstractPersonalAccountProcessor;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClosePersonalAccountProcessor extends AbstractPersonalAccountProcessor {

    @Override
    protected boolean hasSupportType(PersonalAccountType depositType) {
        return true;
    }

    @Override
    protected void processAccount(PersonalAccount account) {
        //TODO:close account after big time unable
//        if (checkToClose(account)) {
//            account.setEnabled(false);
//            depositService.update(account);
//        }
    }

//    private boolean checkToClose(Deposit deposit) {
//        int months = deposit.getMonths();
//        LocalDate startDepositDate = deposit.getStartDepositDate();
//        LocalDate stopDepositDate = startDepositDate.plusMonths(months);
//        LocalDate now = LocalDate.now();
//
//        int maxMonths = Period.between(startDepositDate, stopDepositDate).getMonths();
//        return Period.between(startDepositDate, now).getMonths() >= maxMonths;
//    }
}
