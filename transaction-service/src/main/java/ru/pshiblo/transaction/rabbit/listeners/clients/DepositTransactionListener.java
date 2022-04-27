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
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.repository.TransactionRepository;

import javax.validation.Valid;
import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepositTransactionListener {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.deposit",
                    value = @Queue("transaction.deposit"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
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

            rabbitTemplate.convertAndSend(
                    transaction.getType() == TransactionType.PAYMENT ?
                            "transaction.hold" : "transaction.close",
                    transaction);

        }
    }

}
