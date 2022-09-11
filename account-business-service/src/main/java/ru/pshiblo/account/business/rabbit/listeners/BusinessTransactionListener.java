package ru.pshiblo.account.business.rabbit.listeners;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.business.model.Transaction;
import ru.pshiblo.account.business.services.BusinessAccountService;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.common.exception.NotFoundException;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusinessTransactionListener {

    private final BusinessAccountService service;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;

    
    
    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.close.after.business",
                    value = @Queue("transaction.close.after.business_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void businessAfterSendListener(@Payload Transaction transaction) {
        service.getByNumber(transaction.getFromNumber()).ifPresent(account -> {
            log.info(account.toString());
        });
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.commission.business",
                    value = @Queue("transaction.commission.business_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void businessCommissionListener(@Payload Transaction transaction) {
        if (!transaction.isInnerTo()) {
            Optional.ofNullable(
                    service.getByNumber(transaction.getFromNumber())
                            .orElseThrow(NotFoundException::new)
                            .getType()
                            .getCommissionRateWithdraw()
            ).ifPresent(commissionRate -> {
                transaction.setCommissionRate(commissionRate);
                BigDecimal commissionValue = transaction.getMoney().multiply(transaction.getCommissionRate());
                transaction.setCommissionValue(commissionValue);

                BigDecimal moneyWithCommission = transaction.getMoney().add(commissionValue);
                transaction.setMoneyWithCommission(moneyWithCommission);
            });
        }

        transaction.setStatus("START_FROM_CHECK");
        rabbitTemplate.convertAndSend("transaction.check_from", transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.check_from.business",
                    value = @Queue("transaction.check_from.business_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void businessFromCheckListener(@Payload Transaction transaction) {
        Account account = accountService.getByNumber(transaction.getFromNumber());
        if (account.getLock()) {
            throw new TransactionNotAllowedException("Счет отправителя заблокирован!");
        }
        if (transaction.isInnerTo()) {
            throw new TransactionNotAllowedException("С бизнес счета можно только выводить средства");
        }
        transaction.setApproveSend(true);
        transaction.setStatus("START_SEND");
        rabbitTemplate.convertAndSend("transaction.withdraw", transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.check_to.business",
                    value = @Queue("transaction.check_to.business_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void businessToCheckListener(@Payload Transaction transaction) {

        Account account = accountService.getByNumber(transaction.getToNumber());
        if (account.getLock()) {
            throw new TransactionNotAllowedException("Счет получателя заблокирован!");
        }

        transaction.setApproveAddMoney(true);
        transaction.setStatus("START_ADD_MONEY");
        rabbitTemplate.convertAndSend("transaction.deposit", transaction);
    }
}
