package ru.pshiblo.transaction.web.controllers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.security.jwt.AuthUser;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.kafka.KafkaTopics;
import ru.pshiblo.transaction.web.dto.OpenToAccountTransactionDto;
import ru.pshiblo.transaction.web.dto.OpenToCardTransactionDto;

/**
 * @author Maxim Pshiblo
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/transaction")
public class TransactionsController {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("to/card")
    public void openToCardTransaction(OpenToCardTransactionDto dto, @AuthenticationPrincipal AuthUser authUser) {
        Transaction newTransaction = new Transaction();
        newTransaction.setCurrency(Currency.RUB);
        newTransaction.setToCard(true);
        newTransaction.setInner(true);
        newTransaction.setMoney(dto.getMoney());
        newTransaction.setToNumber(dto.getToCardNumber());
        newTransaction.setFromNumber(dto.getFromCardNumber());
        newTransaction.setOwnerUserId(authUser.getId());
        kafkaTemplate.send(KafkaTopics.OPEN, newTransaction);
    }

    @PostMapping("to/account")
    public void openToAccountTransaction(OpenToAccountTransactionDto dto, @AuthenticationPrincipal AuthUser authUser) {
        Transaction newTransaction = new Transaction();
        newTransaction.setCurrency(Currency.RUB);
        newTransaction.setToCard(false);
        newTransaction.setInner(true);
        newTransaction.setMoney(dto.getMoney());
        newTransaction.setToNumber(dto.getToAccountNumber());
        newTransaction.setFromNumber(dto.getFromCardNumber());
        newTransaction.setOwnerUserId(authUser.getId());
        kafkaTemplate.send(KafkaTopics.OPEN, newTransaction);
    }



}
