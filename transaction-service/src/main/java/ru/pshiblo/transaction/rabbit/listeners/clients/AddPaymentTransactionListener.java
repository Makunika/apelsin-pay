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
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;

import javax.validation.Valid;
import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class AddPaymentTransactionListener {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.ADD_PAYMENT_ROUTE,
                    value = @Queue(RabbitConsts.ADD_PAYMENT_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void applyPaymentTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_ADD_MONEY || !transaction.isApproveAddMoney()) {
            throw new TransactionNotAllowedException("status on send not START_ADD_MONEY or not approve add money");
        }
        Account toAccount = accountService.getByNumber(transaction.getToNumber());

        Currency transactionCurrency = transaction.getCurrency();
        log.info(transaction.toString());
        Currency toAccountCurrency = toAccount.getCurrency();

        BigDecimal moneyCurrentTo = currencyService.convertMoney(
                transactionCurrency,
                toAccountCurrency,
                transaction.getMoney()
        );

        if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
            toAccount.setBalance(accountService.getById(toAccount.getId()).getBalance().add(moneyCurrentTo));
            accountService.save(toAccount);
            transaction.setStatus(TransactionStatus.END_ADD_MONEY);
            transaction = transactionRepository.save(transaction);
            rabbitTemplate.convertAndSend(RabbitConsts.CLOSE_ROUTE, transaction);
        }
    }

}
