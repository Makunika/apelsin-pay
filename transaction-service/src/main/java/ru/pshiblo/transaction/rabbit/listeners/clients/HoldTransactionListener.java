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
import ru.pshiblo.account.domain.HoldMoney;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class HoldTransactionListener {

    private final TransactionRepository repository;
    private final AccountService accountService;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.hold",
                    value = @Queue("transaction.hold_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void holdTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.END_ADD_MONEY) {
            throw new TransactionNotAllowedException("Not status END_ADD_MONEY in hold listener");
        }

        if (transaction.getType() != TransactionType.PAYMENT) {
            throw new TransactionNotAllowedException("Not type PAYMENT in hold listener");
        }

        BigDecimal money = transaction.getMoney();
        HoldMoney holdMoney = accountService.holdMoney(
                accountService.getByNumber(transaction.getToNumber()),
                money,
                LocalDateTime.now().plusHours(10)
        );

        transaction.setStatus(TransactionStatus.HOLD);
        transaction.setHoldId(holdMoney.getId());
        repository.save(transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.unhold",
                    value = @Queue("transaction.unhold_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void unHoldTransaction(@Payload Integer transactionId) {
        repository.findById(transactionId).ifPresent(transaction -> {
            if (transaction.getStatus() != TransactionStatus.HOLD) {
                throw new TransactionNotAllowedException("Not status HOLD in unhold listener");
            }

            accountService.unHoldMoney(transaction.getHoldId());

            rabbitTemplate.convertAndSend("transaction.close", transaction);
        });

    }
}
