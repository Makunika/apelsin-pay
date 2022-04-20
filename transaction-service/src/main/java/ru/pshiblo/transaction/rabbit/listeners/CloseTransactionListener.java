package ru.pshiblo.transaction.rabbit.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.tinkoff.TinkoffApi;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class CloseTransactionListener {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final RabbitTemplate rabbitTemplate;
    private final TinkoffApi tinkoffApi;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CLOSE_ROUTE,
                    value = @Queue(RabbitConsts.CLOSE_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void closeTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.END_SEND || transaction.getStatus() == TransactionStatus.END_ADD_MONEY) {
            transaction.setStatus(TransactionStatus.CLOSED);
            transactionRepository.save(transaction);

            if (transaction.isInnerTo()) {
                switch (transaction.getAccountTypeTo()) {
                    case BUSINESS:
                        rabbitTemplate.convertAndSend(RabbitConsts.CARD_AFTER_SEND_ROUTE, transaction);
                        break;
                    case PERSONAL:
                        rabbitTemplate.convertAndSend(RabbitConsts.DEPOSIT_AFTER_SEND_ROUTE, transaction);
                        break;
                }
            }
        } else {
            throw new TransactionNotAllowedException("Not status END_SEND in close listener");
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.CANCEL_ROUTE,
                    value = @Queue(RabbitConsts.CANCEL_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void cancelTransaction(@Payload Transaction transaction) {

        if (transaction.getStatus().isDepositedMoney() || transaction.getStatus().isWithdrawnMoney()) {
            cancelTransactionWithMoney(transaction);
        }

        transaction.setStatus(TransactionStatus.CANCELED);
        transactionRepository.save(transaction);
    }


    @Transactional
    protected void cancelTransactionWithMoney(Transaction transaction) {
        if (transaction.getStatus().isWithdrawnMoney()) {
            accountService.findByNumber(transaction.getFromNumber()).ifPresent(account -> {
                Currency transactionCurrency = transaction.getCurrency();
                Currency fromAccountCurrency = account.getCurrency();

                BigDecimal moneyCurrentFrom = currencyService.convertMoney(
                        transactionCurrency,
                        fromAccountCurrency,
                        Optional.ofNullable(transaction.getMoneyWithCommission()).orElse(transaction.getMoney())
                );

                account.setBalance(account.getBalance().add(moneyCurrentFrom));
                accountService.save(account);
            });
        }
        if (transaction.getStatus().isDepositedMoney()) {
            accountService.findByNumber(transaction.getToNumber()).ifPresent(account -> {
                Currency transactionCurrency = transaction.getCurrency();
                Currency toAccountCurrency = account.getCurrency();

                BigDecimal moneyCurrentFrom = currencyService.convertMoney(
                        transactionCurrency,
                        toAccountCurrency,
                        transaction.getMoney()
                );

                account.setBalance(account.getBalance().subtract(moneyCurrentFrom));
                accountService.save(account);
            });
        }
    }

}
