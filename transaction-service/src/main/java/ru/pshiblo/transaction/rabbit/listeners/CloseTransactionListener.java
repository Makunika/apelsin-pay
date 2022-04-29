package ru.pshiblo.transaction.rabbit.listeners;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.ExchangeTypes;
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
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.tinkoff.client.TinkoffApi;

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
                    key = "transaction.close",
                    value = @Queue("close_t_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void closeTransaction(@Payload Transaction transaction) {
        if (
                transaction.getStatus() != TransactionStatus.END_SEND
                && transaction.getStatus() != TransactionStatus.END_ADD_MONEY
                && transaction.getStatus() != TransactionStatus.HOLD
        ) {
            throw new TransactionNotAllowedException("Not status END_SEND or END_ADD_MONEY in close listener");
        }
        transaction.setStatus(TransactionStatus.CLOSED);
        transactionRepository.save(transaction);

        if (transaction.isInnerFrom()) {
            switch (transaction.getAccountTypeTo()) {
                case BUSINESS:
                    rabbitTemplate.convertAndSend("transaction.close.after.business", transaction);
                    break;
                case PERSONAL:
                    rabbitTemplate.convertAndSend("transaction.close.after.personal", transaction);
                    break;
            }
        }
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.cancel",
                    value = @Queue("cancel_t_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void cancelTransaction(@Payload TransactionError error) {
        Transaction transaction = transactionRepository.findById(error.getTransactionId()).orElseThrow();

        transaction.setReasonCancel(error.getReason());

        if (transaction.getType() == null) {
            transaction.setType(TransactionType.TRANSFER);
        }

        if (transaction.getStatus().isDepositedMoney() || transaction.getStatus().isWithdrawnMoney()) {
            cancelTransactionWithMoney(transaction);
        }

        transaction.setStatus(TransactionStatus.CANCELED);
        Transaction saved = transactionRepository.save(transaction);
        rabbitTemplate.convertAndSend("transaction.cancel.after", saved);
    }

    @Data
    private static class TransactionError {
        private String reason;
        private Integer transactionId;
    }


    @Transactional
    protected void cancelTransactionWithMoney(Transaction transaction) {
        if (!transaction.isInnerFrom() || !transaction.isInnerTo()) {
            return;
        }

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

        if (transaction.getHoldId() != null) {
            accountService.unHoldMoney(transaction.getHoldId());
        }
    }

}
