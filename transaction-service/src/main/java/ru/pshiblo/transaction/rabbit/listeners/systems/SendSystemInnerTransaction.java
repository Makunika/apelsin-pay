package ru.pshiblo.transaction.rabbit.listeners.systems;

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
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CardService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SendSystemInnerTransaction {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.SEND_SYSTEM_ROUTE,
                    value = @Queue(RabbitConsts.SEND_SYSTEM_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void sendTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_SEND) {
            throw new TransactionNotAllowedException("status on open not START_send");
        }

        if (!transaction.isInner()) {
            throw new TransactionNotAllowedException("System transaction only inner!");
        }

        Account toAccount = accountService.getByNumber(transaction.getToNumber());

        Currency transactionCurrency = transaction.getCurrency();
        log.info(transaction.toString());
        Currency toAccountCurrency = toAccount.getCurrency();

        BigDecimal moneyCurrent = currencyService.convertMoney(
                transactionCurrency,
                toAccountCurrency,
                transaction.getMoney()
        );

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            toAccount.setBalance(accountService.getById(toAccount.getId()).getBalance().add(moneyCurrent));
            accountService.save(toAccount);

            transaction.setStatus(TransactionStatus.END_SEND);
            log.info(transaction.toString());
            transaction = transactionRepository.save(transaction);
            log.info(transaction.toString());

            rabbitTemplate.convertAndSend(RabbitConsts.CLOSE_ROUTE, transaction);
        }
    }

}
