package ru.pshiblo.deposit.rabbit.listeners;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.account.service.DepositService;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.deposit.model.Transaction;
import ru.pshiblo.deposit.rabbit.RabbitConsts;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DepositTransactionListener {

    private final DepositService service;
    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.DEPOSIT_AFTER_SEND_ROUTE,
                    value = @Queue(RabbitConsts.DEPOSIT_AFTER_SEND_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void depositAfterSendListener(@Payload Transaction transaction) {
        service.getByNumber(transaction.getToNumber()).ifPresent(deposit -> {

            if (!deposit.isEnabled() && deposit.getStartDepositDate() == null) {
                BigDecimal minSum = deposit.getDepositType().getMinSum();
                BigDecimal balance = deposit.getAccount().getBalance();
                if (minSum.compareTo(balance) <= 0) {
                    deposit.setEnabled(true);
                    deposit.setStartDepositDate(LocalDate.now());
                    service.update(deposit);
                }
            }

        });
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.DEPOSIT_FROM_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.DEPOSIT_CHECK_FROM_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void depositFromCheckListener(@Payload Transaction transaction) {

        Deposit deposit = service.getByNumber(transaction.getFromNumber())
                .orElseThrow(() -> new NotFoundException(transaction.getFromNumber(), Deposit.class));

        BigDecimal balance = deposit.getAccount().getBalance();

        BigDecimal money = currencyService.convertMoney(
                transaction.getCurrency(),
                transaction.getCurrencyFrom(),
                Optional.ofNullable(transaction.getMoneyWithCommission())
                        .orElse(transaction.getMoney())
        );

        if (balance.subtract(money).compareTo(deposit.getDepositType().getMinSum()) < 0) {
            throw new TransactionNotAllowedException("Меньше минимальной суммы нельзя!");
        }

        transaction.setApproveSend(true);
        transaction.setStatus("START_SEND");
        rabbitTemplate.convertAndSend(RabbitConsts.SEND_ROUTE, transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.DEPOSIT_TO_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.DEPOSIT_CHECK_TO_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void depositToCheckListener(@Payload Transaction transaction) {

        Deposit deposit = service.getByNumber(transaction.getToNumber())
                .orElseThrow(() -> new NotFoundException(transaction.getToNumber(), Deposit.class));

        if (!deposit.isEnabled() && deposit.getStartDepositDate() != null) {
            throw new TransactionNotAllowedException("Депозит уже не работает!");
        }

        transaction.setApproveAddMoney(true);
        transaction.setStatus("START_ADD_MONEY");
        rabbitTemplate.convertAndSend(RabbitConsts.ADD_PAYMENT_ROUTE, transaction);
    }
}
