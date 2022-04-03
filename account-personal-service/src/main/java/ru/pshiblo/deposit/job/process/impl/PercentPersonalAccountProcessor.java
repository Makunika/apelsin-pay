package ru.pshiblo.deposit.job.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.deposit.clients.TransactionClient;
import ru.pshiblo.deposit.core.domain.PersonalAccount;
import ru.pshiblo.deposit.core.domain.PersonalAccountType;
import ru.pshiblo.deposit.model.MoneyEdit;
import ru.pshiblo.deposit.model.Transaction;
import ru.pshiblo.deposit.job.process.AbstractPersonalAccountProcessor;

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
