package ru.pshiblo.account.business.rabbit.listeners;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.business.model.Transaction;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.account.business.rabbit.RabbitConsts;
import ru.pshiblo.account.business.services.BusinessAccountService;

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
                    key = RabbitConsts.BUSINESS_AFTER_SEND_ROUTE,
                    value = @Queue(RabbitConsts.BUSINESS_AFTER_SEND_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void businessAfterSendListener(@Payload Transaction transaction) {
        service.getByNumber(transaction.getToCardNumber()).ifPresent(account -> {
            log.info(account.toString());
        });
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.BUSINESS_FROM_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.BUSINESS_CHECK_FROM_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void businessFromCheckListener(@Payload Transaction transaction) {
        Account account = accountService.getByNumber(transaction.getFromNumber());
        if (account.getLock()) {
            throw new TransactionNotAllowedException("Счет отправителя заблокирован!");
        }
        //TODO: придумать что то с типом!

        transaction.setApproveSend(true);
        transaction.setStatus("START_SEND");
        rabbitTemplate.convertAndSend(RabbitConsts.SEND_ROUTE, transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.BUSINESS_TO_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.BUSINESS_CHECK_TO_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
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
        rabbitTemplate.convertAndSend(RabbitConsts.ADD_PAYMENT_ROUTE, transaction);
    }
}
