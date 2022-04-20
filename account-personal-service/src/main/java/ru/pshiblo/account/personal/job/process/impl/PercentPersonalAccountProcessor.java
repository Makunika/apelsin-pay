package ru.pshiblo.account.personal.job.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.personal.clients.TransactionClient;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;
import ru.pshiblo.account.personal.model.MoneyEdit;
import ru.pshiblo.account.personal.model.Transaction;
import ru.pshiblo.account.personal.job.process.AbstractPersonalAccountProcessor;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PercentPersonalAccountProcessor extends AbstractPersonalAccountProcessor {

    private final TransactionClient transactionClient;

    @Override
    protected boolean hasSupportType(PersonalAccountType type) {
        return true;
    }

    @Override
    protected void processAccount(PersonalAccount account) {
        PersonalAccountType type = account.getType();
        int percentCashback = type.getPercentCashback();

        BigDecimal addedMoney = account
                .getAccount()
                .getBalance()
                .multiply(BigDecimal.valueOf(percentCashback / 100.));

        MoneyEdit moneyEdit = new MoneyEdit();
        moneyEdit.setCurrency(type.getCurrency());
        moneyEdit.setAccountNumber(account.getAccount().getNumber());
        moneyEdit.setBalanceEdit(addedMoney);
        Transaction transactionDto = transactionClient.editMoneyToAccount(moneyEdit);
        log.info(transactionDto.toString());
    }
}
