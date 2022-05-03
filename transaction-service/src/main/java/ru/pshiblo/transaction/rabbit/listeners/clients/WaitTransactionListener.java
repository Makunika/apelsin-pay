package ru.pshiblo.transaction.rabbit.listeners.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.tinkoff.client.TinkoffApi;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffInvoicingResponse;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffInvoicingStatusResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class WaitTransactionListener {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;
    private final TinkoffApi tinkoffApi;
    private final ObjectMapper objectMapper;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.wait",
                    value = @Queue("transaction.wait_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void waitTransaction(@Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_TO_CHECK) {
            throw new TransactionNotAllowedException("Not status START_TO_CHECK in hold listener");
        }

        if (transaction.isInnerFrom()) {
            throw new TransactionNotAllowedException("Not type PAYMENT in wait listener");
        }

        transaction.setStatus(TransactionStatus.WAIT);
        repository.save(transaction);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.unwait",
                    value = @Queue("transaction.unwait_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    public void unWaitTransaction(@Payload Integer transactionId) {
        Transaction transaction = repository.findById(transactionId).orElse(null);
        if (transaction == null) {
            return;
        }

        if (transaction.getStatus() != TransactionStatus.WAIT) {
            throw new TransactionNotAllowedException("Not status WAIT in unwait listener");
        }

        String tinkoffPaymentId = transaction.getTinkoffPaymentId();
        TinkoffInvoicingStatusResponse statusInvoicing = tinkoffApi.getStatusInvoicing(tinkoffPaymentId);
        if (!statusInvoicing.getStatus().isSuccess()) {
            throw new TransactionNotAllowedException("Tinkoff not confirmed");
        }

        transaction.setStatus(TransactionStatus.START_TO_CHECK);
        rabbitTemplate.convertAndSend("transaction.check_to", transaction);
    }
}
