package ru.pshiblo.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.IntegrationException;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionStatus;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.repository.TransactionRepository;
import ru.pshiblo.transaction.tinkoff.client.TinkoffApi;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffInvoicingCreate;
import ru.pshiblo.transaction.tinkoff.model.request.TinkoffInvoicingDescription;
import ru.pshiblo.transaction.tinkoff.model.response.TinkoffInvoicingResponse;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final RabbitTemplate rabbitTemplate;
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
    public Transaction createFromTinkoff(Transaction transaction, String redirectUrl) {
        transaction.setStatus(TransactionStatus.START_OPEN);
        Transaction savedTransaction = repository.save(transaction);
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
            savedTransaction.setTinkoffPayUrl(response.getPaymentUrl());
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
    public List<Transaction> getByUserId(int userId) {
        return repository.findAllByOwnerUserId(userId);
    }

    @Override
    public Optional<TransactionStatus> getStatusById(int id) {
        return getById(id).map(Transaction::getStatus);
    }
}
