package ru.pshiblo.transaction.rabbit.listeners;

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
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CardService;
import ru.pshiblo.account.service.CurrencyService;

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
    private final CardService cardService;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.SEND_ROUTE,
                    value = @Queue(RabbitConsts.SEND_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void sendTransaction(@Payload Transaction transaction) throws NotSupportedException {
        if (transaction.getStatus() == TransactionStatus.START_SEND) {
            transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);

            if (transaction.isInner()) {
                Account toAccount = transaction.isToCard() ?
                        cardService.getByNumber(transaction.getToNumber()).getAccount() :
                        accountService.getByNumber(transaction.getToNumber());

                Account fromAccount = accountService.getByNumber(transaction.getFromNumber());

                Currency transactionCurrency = transaction.getCurrency();
                if (transactionCurrency == null) {
                    log.info("!!!! null currency");
                }
                log.info(transaction.toString());
                Currency toAccountCurrency = toAccount.getCurrency();
                Currency fromAccountCurrency = fromAccount.getCurrency();

                BigDecimal moneyCurrentFrom = currencyService.convertMoney(
                        transactionCurrency,
                        fromAccountCurrency,
                        Optional.ofNullable(transaction.getMoneyWithCommission()).orElse(transaction.getMoney())
                );

                BigDecimal moneyCurrentTo = currencyService.convertMoney(
                        transactionCurrency,
                        toAccountCurrency,
                        transaction.getMoney()
                );

                if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
                    if (fromAccount.getBalance().compareTo(moneyCurrentFrom) > 0) {
                        toAccount.setBalance(accountService.getById(toAccount.getId()).getBalance().add(moneyCurrentTo));
                        accountService.save(toAccount);
                        fromAccount.setBalance(accountService.getById(fromAccount.getId()).getBalance().subtract(moneyCurrentFrom));
                        accountService.save(fromAccount);

                        transaction.setStatus(TransactionStatus.END_SEND);
                        log.info(transaction.toString());
                        transaction = transactionRepository.save(transaction);
                        log.info(transaction.toString());
                        log.info("FINISH");

                        rabbitTemplate.convertAndSend(RabbitConsts.CLOSE_ROUTE, transaction);
                    } else {
                        throw new TransactionNotAllowedException();
                    }
                }
            } else {
                throw new NotSupportedException("only is inner");
            }
        } else {
            throw new TransactionNotAllowedException("status on send not START_SEND");
        }
    }
}
