package ru.pshiblo.card.rabbit.listeners;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.Card;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CardService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.card.model.Transaction;
import ru.pshiblo.card.rabbit.RabbitConsts;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardTransactionListener {

    private final CardService service;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CARD_AFTER_SEND_ROUTE,
                    value = @Queue(RabbitConsts.CARD_AFTER_SEND_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void cardAfterSendListener(@Payload Transaction transaction) {
        service.findByNumber(transaction.getToCardNumber()).ifPresent(card -> {
            log.info(card.toString());
        });
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CARD_FROM_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.CARD_CHECK_FROM_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void cardFromCheckListener(@Payload Transaction transaction) {

        Account account = accountService.getByNumber(transaction.getFromNumber());
        if (transaction.getFromCardNumber() == null) {
            List<Card> cards = service.getByAccountNumber(account.getNumber());
            if (cards
                    .stream()
                    .allMatch(Card::isExpiredOrLock)
            ) {
                throw new TransactionNotAllowedException("Все карты заблокированы или закончили срок действия");
            }
        } else {
            Card card = service.getByNumber(transaction.getFromCardNumber());
            if (card.isExpiredOrLock()) {
                throw new TransactionNotAllowedException("Карта заблокирована");
            }
        }

        transaction.setApproveSend(true);
        transaction.setStatus("START_SEND");
        rabbitTemplate.convertAndSend(RabbitConsts.SEND_ROUTE, transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CARD_TO_CHECK_ROUTE,
                    value = @Queue(RabbitConsts.CARD_CHECK_TO_Q),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void cardToCheckListener(@Payload Transaction transaction) {

        Account account = accountService.getByNumber(transaction.getToNumber());
        if (transaction.getToCardNumber() == null) {
            List<Card> cards = service.getByAccountNumber(account.getNumber());
            if (cards.isEmpty() || cards
                    .stream()
                    .allMatch(Card::isExpiredOrLock)
            ) {
                throw new TransactionNotAllowedException("Все карты заблокированы или закончили срок действия");
            }
        } else {
            Card card = service.getByNumber(transaction.getToCardNumber());
            if (card.isExpiredOrLock()) {
                throw new TransactionNotAllowedException("Карта заблокирована");
            }
        }

        transaction.setApproveAddMoney(true);
        transaction.setStatus("START_ADD_MONEY");
        rabbitTemplate.convertAndSend(RabbitConsts.ADD_PAYMENT_ROUTE, transaction);
    }
}
