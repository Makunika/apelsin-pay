package ru.pshiblo.transaction.web.controllers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.web.dto.request.OpenToAccountTransactionDto;
import ru.pshiblo.transaction.web.dto.request.OpenToCardTransactionDto;

/**
 * @author Maxim Pshiblo
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/transaction")
public class TransactionsController {

    private final TransactionService transactionService;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("to/card")
    public Transaction openToCardTransaction(@RequestBody OpenToCardTransactionDto dto) {
        Transaction newTransaction = new Transaction();
        newTransaction.setCurrency(dto.getCurrency());
        newTransaction.setToCard(true);
        newTransaction.setInner(true);
        newTransaction.setMoney(dto.getMoney());
        newTransaction.setToNumber(dto.getToCardNumber());
        newTransaction.setFromNumber(dto.getFromAccountNumber());
        newTransaction.setOwnerUserId((int) AuthUtils.getUserId());

        return transactionService.create(newTransaction);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("to/account")
    public Transaction openToAccountTransaction(@RequestBody OpenToAccountTransactionDto dto) {
        Transaction newTransaction = new Transaction();
        newTransaction.setCurrency(dto.getCurrency());
        newTransaction.setToCard(false);
        newTransaction.setInner(true);
        newTransaction.setMoney(dto.getMoney());
        newTransaction.setToNumber(dto.getToAccountNumber());
        newTransaction.setFromNumber(dto.getFromAccountNumber());
        newTransaction.setOwnerUserId((int) AuthUtils.getUserId());

        return transactionService.create(newTransaction);
    }



}
