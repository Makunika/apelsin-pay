package ru.pshiblo.account.personal.rabbit.listeners;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.personal.core.services.PersonalAccountService;
import ru.pshiblo.account.personal.model.Transaction;
import ru.pshiblo.account.personal.rabbit.RabbitConsts;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PersonalTransactionListener {

    private final PersonalAccountService service;
    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.PERSONAL_AFTER_SEND_ROUTE,
                    value = @Queue(RabbitConsts.PERSONAL_AFTER_SEND_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void personalAfterSendListener(@Payload Transaction transaction) {
        //TODO: ADD CASHBACK
        service.getByNumber(transaction.getToNumber()).ifPresent(account -> {
            if (!account.getIsEnabled() && account.getStartWork() == null) {
                BigDecimal minSumToStartWork = account.getType().getMinSumToStartWork();
                BigDecimal balance = account.getAccount().getBalance();
                if (balance.compareTo(minSumToStartWork) <= 0) {
                    account.setIsEnabled(true);
                    account.setStartWork(LocalDateTime.now());
                    service.update(account);
                }
            }
        });
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.PERSONAL_FROM_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.PERSONAL_CHECK_FROM_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void personalFromCheckListener(@Payload Transaction transaction) {

        PersonalAccount account = service.getByNumber(transaction.getFromNumber())
                .orElseThrow(() -> new NotFoundException(transaction.getFromNumber(), PersonalAccount.class));

        if (!account.getIsEnabled()) {
            throw new TransactionNotAllowedException("Счет не работает!");
        }

        BigDecimal maxSumForPay = account.getType().getMaxSumForPay();

        BigDecimal money = currencyService.convertMoney(
                transaction.getCurrency(),
                account.getAccount().getCurrency(),
                transaction.getMoney()
        );

        if (maxSumForPay.compareTo(money) < 0) {
            throw new TransactionNotAllowedException("Больше максимальной суммы для перевода по тарифу!");
        }

        transaction.setApproveSend(true);
        transaction.setStatus("START_SEND");
        rabbitTemplate.convertAndSend(RabbitConsts.SEND_ROUTE, transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.PERSONAL_TO_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.PERSONAL_CHECK_TO_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void personalToCheckListener(@Payload Transaction transaction) {
        PersonalAccount account = service.getByNumber(transaction.getToNumber())
                .orElseThrow(() -> new NotFoundException(transaction.getToNumber(), PersonalAccount.class));

        BigDecimal balance = account.getAccount().getBalance();

        BigDecimal maxSum = account.getType().getMaxSum();

        BigDecimal money = currencyService.convertMoney(
                transaction.getCurrency(),
                account.getAccount().getCurrency(),
                Optional.ofNullable(transaction.getMoneyWithCommission())
                        .orElse(transaction.getMoney())
        );

        if (maxSum.compareTo(balance.add(money)) < 0) {
            throw new TransactionNotAllowedException("Превышен лимит денег на карте!");
        }

        transaction.setApproveAddMoney(true);
        transaction.setStatus("START_ADD_MONEY");
        rabbitTemplate.convertAndSend(RabbitConsts.ADD_PAYMENT_ROUTE, transaction);
    }
}
