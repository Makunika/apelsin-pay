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
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.common.exception.InternalException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.account.exceptions.TransactionNotAllowedException;
import ru.pshiblo.transaction.model.PayoutModel;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.transaction.tinkoff.builders.TinkoffPaymentBuilder;
import ru.pshiblo.transaction.tinkoff.client.TinkoffApi;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffPayment;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffTo;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WithdrawTransactionListener {

    private final AccountService accountService;
    private final TransactionRepository transactionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;
    private final TinkoffApi tinkoffApi;
    private final ObjectMapper objectMapper;
    private final TinkoffPaymentBuilder tinkoffPaymentBuilder;

    @RabbitListener(
            bindings = @QueueBinding(
                    key = "transaction.withdraw",
                    value = @Queue("transaction.withdraw_q"),
                    exchange = @Exchange(type = ExchangeTypes.TOPIC, name = "exchange-main")
            ),
            errorHandler = "errorTransactionHandler"
    )
    @Transactional
    public void sendTransaction(@Valid @Payload Transaction transaction) {
        if (transaction.getStatus() != TransactionStatus.START_SEND || !transaction.isApproveSend()) {
            throw new TransactionNotAllowedException("status on send not START_SEND or not approved");
        }

        transactionRepository.findById(transaction.getId()).orElseThrow(NotFoundException::new);

        if (transaction.isInnerTo()) {
            innerSendTransaction(transaction);
        } else {
            extendSendTransaction(transaction);
        }
    }

    private void withdraw(Transaction transaction) {
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
            } else {
                throw new TransactionNotAllowedException("Balance small for withdraw");
            }
        }
    }


    private void innerSendTransaction(Transaction transaction) {
        withdraw(transaction);
        transaction.setStatus(TransactionStatus.END_SEND);
        transaction = transactionRepository.save(transaction);
        transaction.setStatus(TransactionStatus.START_APPLY_PAYMENT);
        rabbitTemplate.convertAndSend("transaction.apply_payment", transaction);
    }

    private void extendSendTransaction(Transaction transaction) {
        try {
            if (transaction.getCurrency() != Currency.RUB) {
                throw new TransactionNotAllowedException("Currency to required RUB");
            }

            PayoutModel payoutModel = objectMapper.readValue(transaction.getAdditionInfoTo(), PayoutModel.class);
            TinkoffPayment payment;
            if (payoutModel.getIsPerson()) {
                payment = tinkoffPaymentBuilder.builder()
                        .amountRubles(transaction.getMoney())
                        .id(transaction.getId())
                        .toPersonal(
                                TinkoffTo.builder()
                                        .accountNumber(payoutModel.getAccountNumber())
                                        .bankName(payoutModel.getBankName())
                                        .bik(payoutModel.getBik())
                                        .name(payoutModel.getName())
                                        .corrAccountNumber(payoutModel.getCorrAccountNumber())
                                        .build()
                        )
                        .build();
            } else {
                payment = tinkoffPaymentBuilder.builder()
                        .amountRubles(transaction.getMoney())
                        .id(transaction.getId())
                        .toBusiness(
                                TinkoffTo.builder()
                                        .accountNumber(payoutModel.getAccountNumber())
                                        .bankName(payoutModel.getBankName())
                                        .bik(payoutModel.getBik())
                                        .name(payoutModel.getName())
                                        .corrAccountNumber(payoutModel.getCorrAccountNumber())
                                        .inn(payoutModel.getInn())
                                        .kpp(payoutModel.getKpp())
                                        .build()
                        )
                        .build();
            }
            tinkoffApi.paymentTo(payment);
            withdraw(transaction);
            transaction.setStatus(TransactionStatus.END_SEND);
            transaction = transactionRepository.save(transaction);
            rabbitTemplate.convertAndSend("transaction.close", transaction);
        } catch (JsonProcessingException e) {
            throw new InternalException(e.getMessage(), e);
        }

    }
}
