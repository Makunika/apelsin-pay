package ru.pshiblo.transaction.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.common.exception.IntegrationException;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.clients.BusinessAccountClient;
import ru.pshiblo.transaction.clients.PersonalAccountClient;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.model.InfoPrepare;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.tinkoff.client.TinkoffApi;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffInvoicingCreate;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffInvoicingDescription;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffInvoicingResponse;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final AccountService accountService;
    private final PersonalAccountClient personalAccountClient;
    private final BusinessAccountClient businessAccountClient;
    private final RabbitTemplate rabbitTemplate;
    private final CurrencyService currencyService;
    private final TinkoffApi tinkoffApi;
    private final ObjectMapper objectMapper;

    @Override
    public Transaction create(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend("transaction.open", savedTransaction);
        return savedTransaction;
    }

    @Override
    public Transaction createSystem(Transaction transaction) {
        transaction.setStatus(TransactionStatus.START_OPEN);
        transaction.setType(TransactionType.TRANSFER);

        Transaction savedTransaction = repository.save(transaction);
        rabbitTemplate.convertAndSend("transaction.open.system", savedTransaction);
        return savedTransaction;
    }

    @Override
    public Transaction createFromTinkoff(Transaction transaction, String redirectHost) {
        transaction.setStatus(TransactionStatus.START_OPEN);
        Transaction savedTransaction = repository.save(transaction);
        String redirectUrl;

        if (redirectHost.lastIndexOf('?') == -1) {
            redirectUrl = redirectHost + "?" +
                    "number=" + savedTransaction.getToNumber() +
                    "&id=" + savedTransaction.getId();
        } else {
            redirectUrl = redirectHost + "&" +
                    "number=" + savedTransaction.getToNumber() +
                    "&id=" + savedTransaction.getId();
        }

        try {
            TinkoffInvoicingResponse response = tinkoffApi.createInvoicing(
                    TinkoffInvoicingCreate.builder()
                            .amount(transaction.getMoney())
                            .description(TinkoffInvoicingDescription.builder()
                                    .full("Оплата транзакции #" + transaction.getId())
                                    .shortName("Транзакция")
                                    .build())
                            .endDate(DateUtils.addDays(new Date(), 1))
                            .redirectUrl(redirectUrl)
                            .orderId(transaction.getId().toString())
                            .build());
            savedTransaction.setAdditionInfoFrom(objectMapper.writeValueAsString(response));
            savedTransaction.setTinkoffPaymentId(response.getId());
            String testPaymentUrl = "http://localhost:3000/tinkoff/test?" + redirectUrl;
            log.info(response.getPaymentUrl() + " replace to " + testPaymentUrl);
            savedTransaction.setTinkoffPayUrl(testPaymentUrl);
            savedTransaction.setIsWithdrawByTinkoff(false);
            repository.save(savedTransaction);
            rabbitTemplate.convertAndSend("transaction.open", transaction);
        } catch (IntegrationException e)
        {
            log.error(e.getMessage(), e);
            savedTransaction.setStatus(TransactionStatus.CANCELED);
            repository.save(savedTransaction);
            throw new IntegrationException(500, e.getMessage());
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        return savedTransaction;
    }

    @Override
    public void successPayment(int transactionId, long userId) {
        Transaction transaction = repository.findById(transactionId)
                .orElseThrow(() -> new NotFoundException(transactionId, Transaction.class));
        if (transaction.getOwnerUserId().longValue() != userId) {
            throw new NotAllowedOperationException("");
        }
        rabbitTemplate.convertAndSend("transaction.unwait", transaction.getId());
    }

    @Override
    public Optional<Transaction> getById(int id) {
        return repository.findById(id);
    }

    @Override
    public Page<Transaction> getByUserIdAndNumber(int userId, String number, Pageable pageable) {
        Account account = accountService.getByNumber(number);
        checkOwnerAccount(account, userId);
        return repository.findByFromNumberOrToNumberOrderByCreatedDesc(number, number, pageable);
    }

    @Override
    public Optional<TransactionStatus> getStatusById(int id) {
        return getById(id).map(Transaction::getStatus);
    }

    @Override
    public InfoPrepare getPrepareInfo(String toNumber, String fromNumber, BigDecimal money, Currency currency, long userId) {
        InfoPrepare infoPrepare = new InfoPrepare();
        Account accountFrom = accountService.findByNumber(fromNumber).orElseThrow(() -> new NotFoundException(toNumber, "Счет"));
        checkOwnerAccount(accountFrom, userId);

        if (accountFrom.getCurrency() != currency) {
            infoPrepare.setMoneyFrom(currencyService.convertMoney(currency, accountFrom.getCurrency(), money));
            infoPrepare.setCurrencyFrom(accountFrom.getCurrency());
        }
        accountService.findByNumber(toNumber).ifPresent(account -> {
            infoPrepare.setNameTo(account.getOwnerName());
            if (account.getCurrency() != currency) {
                infoPrepare.setMoneyTo(currencyService.convertMoney(currency, account.getCurrency(), money));
                infoPrepare.setCurrencyTo(account.getCurrency());
            }
        });
        infoPrepare.setCurrency(currency);
        infoPrepare.setMoney(money);
        return infoPrepare;
    }

    private void checkOwnerAccount(Account account, long userId) {
        if (account.getType() == AccountType.PERSONAL) {
            if (Boolean.FALSE.equals(personalAccountClient.checkPersonalAccount(userId, account.getNumber()).getBody())) {
                throw new NotAllowedOperationException();
            }
        } else {
            if (Boolean.FALSE.equals(businessAccountClient.checkBusinessAccount(userId, account.getNumber()).getBody())) {
                throw new NotAllowedOperationException();
            }
        }
    }
}
