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
import ru.pshiblo.transaction.service.TransactionBuilder;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.web.dto.request.MoneyDto;
import ru.pshiblo.transaction.web.dto.request.OpenTransactionCvcDto;
import ru.pshiblo.transaction.web.dto.request.OpenTransactionDto;

/**
 * @author Maxim Pshiblo
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/transaction")
public class TransactionsController {

    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/from/account/to/card")
    public Transaction openToCardTransaction(@RequestBody OpenTransactionDto dto) {
        Transaction transaction = transactionBuilder.builderInner()
                .authUser(AuthUtils.getAuthUser())
                .money(dto.getMoney())
                .currency(dto.getCurrency())
                .fromAccount(dto.getFromNumber())
                .toCard(dto.getToNumber())
                .build();
        return transactionService.create(transaction);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/from/card/to/card")
    public Transaction openFromCardToCardTransaction(@RequestBody OpenTransactionDto dto) {
        Transaction transaction = transactionBuilder.builderInner()
                .authUser(AuthUtils.getAuthUser())
                .money(dto.getMoney())
                .currency(dto.getCurrency())
                .fromCard(dto.getFromNumber())
                .toCard(dto.getToNumber())
                .build();
        return transactionService.create(transaction);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/from/card/to/account")
    public Transaction openFromCardToAccountTransaction(@RequestBody OpenTransactionDto dto) {
        Transaction transaction = transactionBuilder.builderInner()
                .authUser(AuthUtils.getAuthUser())
                .money(dto.getMoney())
                .currency(dto.getCurrency())
                .fromCard(dto.getFromNumber())
                .toAccount(dto.getToNumber())
                .build();
        return transactionService.create(transaction);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/from/account/to/account")
    public Transaction openFromAccountToAccountTransaction(@RequestBody OpenTransactionDto dto) {
        Transaction transaction = transactionBuilder.builderInner()
                .authUser(AuthUtils.getAuthUser())
                .money(dto.getMoney())
                .currency(dto.getCurrency())
                .fromAccount(dto.getFromNumber())
                .toAccount(dto.getToNumber())
                .build();
        return transactionService.create(transaction);
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_edit_money')")
    @PostMapping("/admin/edit/money")
    public Transaction editMoneyToAccount(@RequestBody MoneyDto moneyDto) {
        Transaction newTransaction = new Transaction();
        newTransaction.setCurrency(moneyDto.getCurrency());
        newTransaction.setInner(true);
        newTransaction.setMoney(moneyDto.getBalanceEdit());
        newTransaction.setOwnerUserId((int) AuthUtils.getUserId());
        newTransaction.setFromNumber("SYSTEM");
        newTransaction.setOwnerUsername(AuthUtils.getAuthUser().getName());
        newTransaction.setToNumber(moneyDto.getAccountNumber());
        return transactionService.createSystem(newTransaction);
    }

    @PostMapping("/from/card/to/card/cvc")
    public Transaction openFromCardToCardTransactionCvc(@RequestBody OpenTransactionCvcDto dto) {
        Transaction transaction = transactionBuilder.builderInner()
                .cvc(dto.getCvc())
                .money(dto.getMoney())
                .currency(dto.getCurrency())
                .fromCard(dto.getFromNumber())
                .toCard(dto.getToNumber())
                .build();
        return transactionService.create(transaction);
    }

    @PostMapping("/from/card/to/account/cvc")
    public Transaction openFromCardToAccountTransactionCvc(@RequestBody OpenTransactionCvcDto dto) {
        Transaction transaction = transactionBuilder.builderInner()
                .cvc(dto.getCvc())
                .money(dto.getMoney())
                .currency(dto.getCurrency())
                .fromCard(dto.getFromNumber())
                .toAccount(dto.getToNumber())
                .build();
        return transactionService.create(transaction);
    }

}
