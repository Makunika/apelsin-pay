package ru.pshiblo.deposit.job.process.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.domain.DepositType;
import ru.pshiblo.deposit.clients.TransactionClient;
import ru.pshiblo.deposit.model.MoneyEdit;
import ru.pshiblo.deposit.model.Transaction;
import ru.pshiblo.deposit.job.process.AbstractDepositProcessor;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class PercentDepositProcessor extends AbstractDepositProcessor {

    private final TransactionClient transactionClient;

    @Override
    protected boolean hasSupportDepositType(DepositType depositType) {
        return true;
    }

    @Override
    protected void processDeposit(Deposit deposit) {
        DepositType depositType = deposit.getDepositType();
        int percentMonth = depositType.getPercentMonth();

        BigDecimal addedMoney = deposit
                .getAccount()
                .getBalance()
                .multiply(BigDecimal.valueOf(percentMonth / 100.));

        MoneyEdit moneyEdit = new MoneyEdit();
        moneyEdit.setCurrency(depositType.getCurrency());
        moneyEdit.setAccountNumber(deposit.getNumber());
        moneyEdit.setBalanceEdit(addedMoney);
        Transaction transactionDto = transactionClient.editMoneyToAccount(moneyEdit);
        log.info(transactionDto.toString());
    }
}
