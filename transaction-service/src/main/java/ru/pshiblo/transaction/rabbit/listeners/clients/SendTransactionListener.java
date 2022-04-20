package ru.pshiblo.transaction.rabbit.listeners.clients;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.tinkoff.TinkoffApi;

import javax.transaction.NotSupportedException;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SendTransactionListener {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;
    private final TinkoffApi tinkoffApi;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.SEND_ROUTE,
                    value = @Queue(RabbitConsts.SEND_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void sendTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_SEND || !transaction.isApproveSend()) {
            throw new TransactionNotAllowedException("status on send not START_SEND or not approved");
        }

        transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);

        if (transaction.isInnerFrom()) {
            innerSendTransaction(transaction);
        } else {
            extendSendTransaction(transaction);
        }
    }


    private void innerSendTransaction(Transaction transaction) {
        Account fromAccount = accountService.getByNumber(transaction.getFromNumber());

        Currency transactionCurrency = transaction.getCurrency();
        Currency fromAccountCurrency = fromAccount.getCurrency();

        BigDecimal moneyCurrentFrom = currencyService.convertMoney(
                transactionCurrency,
                fromAccountCurrency,
                Optional.ofNullable(transaction.getMoneyWithCommission()).orElse(transaction.getMoney())
        );

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            if (fromAccount.getBalance().compareTo(moneyCurrentFrom) >= 0) {
                fromAccount.setBalance(accountService.getById(fromAccount.getId()).getBalance().subtract(moneyCurrentFrom));
                accountService.save(fromAccount);

                transaction.setStatus(TransactionStatus.END_SEND);
                transaction = transactionRepository.save(transaction);
                transaction.setStatus(TransactionStatus.START_APPLY_PAYMENT);
                rabbitTemplate.convertAndSend(RabbitConsts.APPLY_PAYMENTS_ROUTE, transaction);
            } else {
                throw new TransactionNotAllowedException("Balance small for withdrawn");
            }
        }
    }

    private void extendSendTransaction(Transaction transaction) {
        throw new NotAllowedOperationException("only is inner");
    }
}
