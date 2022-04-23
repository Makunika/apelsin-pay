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
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.rabbit.RabbitConsts;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenSystemInnerTransaction {

    private final RabbitTemplate rabbitTemplate;
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final CurrencyService currencyService;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = RabbitConsts.OPEN_SYSTEM_ROUTE,
                    value = @Queue(RabbitConsts.OPEN_SYSTEM_QUEUE),
                    exchange = @Exchange(RabbitConsts.MAIN_EXCHANGE)
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void openTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() == TransactionStatus.START_OPEN) {
            Account account = accountService.getByNumber(transaction.getToNumber());

            if (transaction.getMoney().compareTo(BigDecimal.ZERO) <= 0) {
                throw new TransactionNotAllowedException("Zero or negative money value");
            }

            if (account.getLock()) {
                throw new TransactionNotAllowedException("Account is lock");
            }

            transaction.setAccountTypeTo(account.getType());
            transaction.setCurrencyTo(account.getCurrency());

            transaction = transactionRepository.save(transaction);

            if (!transactionRepository.existsByStatusAndId(TransactionStatus.CANCELED, transaction.getId())) {
                transaction.setStatus(TransactionStatus.END_OPEN);
                transactionRepository.save(transaction);
                transaction.setStatus(TransactionStatus.START_SEND);
                rabbitTemplate.convertAndSend(RabbitConsts.SEND_SYSTEM_ROUTE, transaction);
            }
        } else {
            throw new TransactionNotAllowedException("status on open not START_OPEN");
        }
    }

}
