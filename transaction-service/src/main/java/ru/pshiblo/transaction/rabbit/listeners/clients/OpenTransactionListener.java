package ru.pshiblo.transaction.rabbit.listeners.clients;

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
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;

import javax.validation.Valid;
import java.math.BigDecimal;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OpenTransactionListener {

    private final RabbitTemplate rabbitTemplate;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CurrencyService currencyService;


    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.open",
                    value = @Queue("open_t_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void openTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_OPEN) {
            throw new TransactionNotAllowedException("status on open not START_OPEN");
        }

        if (transaction.getMoney().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionNotAllowedException("Zero or negative money value");
        }

        if (transaction.isInnerFrom()) {
            Account account = accountService.getByNumber(transaction.getFromNumber());

            if (account.getLock()) {
                throw new TransactionNotAllowedException("Account is lock");
            }

            BigDecimal holdMoney = accountService.getCurrentHoldMoney(account);

            if (account.getBalance().add(holdMoney.negate()).compareTo(
                    currencyService.convertMoney(transaction.getCurrency(), account.getCurrency(), transaction.getMoney())
            ) < 0) {
                throw new TransactionNotAllowedException("Balance small");
            }

            transaction.setCurrencyFrom(account.getCurrency());
            transaction.setAccountTypeFrom(account.getType());
        }

        if (transaction.isInnerTo()) {
            Account toAccount = accountService.getByNumber(transaction.getToNumber());

            if (toAccount.getLock()) {
                throw new TransactionNotAllowedException("To account is lock");
            }

            transaction.setAccountTypeTo(toAccount.getType());
            transaction.setCurrencyTo(toAccount.getCurrency());
        }

        if (transaction.isInnerTo() && transaction.isInnerFrom()) {
            Account account = accountService.getByNumber(transaction.getFromNumber());
            Account toAccount = accountService.getByNumber(transaction.getToNumber());
            if (account.getId().equals(toAccount.getId())) {
                throw new TransactionNotAllowedException("Account equals");
            }
        }

        transaction = transactionRepository.save(transaction);

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            transaction.setStatus(TransactionStatus.END_OPEN);
            transactionRepository.save(transaction);
            if (transaction.isInnerFrom()) {
                transaction.setStatus(TransactionStatus.START_COMMISSION);
                rabbitTemplate.convertAndSend("transaction.commission", transaction);
            } else {
                transaction.setStatus(TransactionStatus.START_APPLY_PAYMENT);
                rabbitTemplate.convertAndSend("transaction.apply_payment", transaction);
            }
        }
    }
}
