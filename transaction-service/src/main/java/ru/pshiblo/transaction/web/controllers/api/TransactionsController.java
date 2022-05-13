package ru.pshiblo.transaction.web.controllers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.mappers.TransactionMapper;
import ru.pshiblo.transaction.model.PayoutModel;
import ru.pshiblo.transaction.service.TransactionBuilder;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.web.dto.request.*;
import ru.pshiblo.transaction.web.dto.response.TransactionResponseDto;

import javax.validation.Valid;
import java.util.List;

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
    private final TransactionMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_transaction_s')")
    @PostMapping("/payment/inner")
    public Transaction openPaymentInner(@Valid @RequestBody OpenPaymentInnerDto request) {
        Transaction transaction = transactionBuilder.builderInner()
                .userId(request.getUserId())
                .money(request.getMoney())
                .currency(request.getCurrency())
                .type(TransactionType.PAYMENT)
                .fromAccount(request.getAccountNumberFrom())
                .toAccount(request.getAccountNumberTo())
                .build();
        return transactionService.create(transaction);
    }

    @PreAuthorize("hasAuthority('SCOPE_transaction_s')")
    @PostMapping("/payment/tinkoff")
    public Transaction openPaymentTinkoff(@Valid @RequestBody OpenPaymentTinkoffDto request) {
        Transaction transaction = transactionBuilder.builderOutFrom()
                .userId(AuthUtils.getUserId())
                .money(request.getMoney())
                .currency(Currency.RUB)
                .type(TransactionType.PAYMENT)
                .toAccount(request.getAccountNumberTo())
                .build();
        return transactionService.createFromTinkoff(transaction, request.getRedirectUrl());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/deposit/tinkoff")
    public Transaction openDepositWithTinkoff(@Valid @RequestBody OpenDepositDto request) {
        Transaction transaction = transactionBuilder.builderOutFrom()
                .authUser(AuthUtils.getAuthUser())
                .money(request.getMoney())
                .currency(Currency.RUB)
                .type(TransactionType.TRANSFER)
                .toAccount(request.getAccountNumberTo())
                .build();
        return transactionService.createFromTinkoff(transaction, "redirect");
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

    @PreAuthorize("hasAnyAuthority('SCOPE_user', 'SCOPE_transaction_s')")
    @PostMapping("/success/redirect/{id}")
    public void confirmTransaction(@PathVariable int id) {
        AuthUser authUser = AuthUtils.getAuthUser();
        transactionService.successPayment(id, authUser.getId());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/from/external/to/account")
    public Transaction openFromCardToAccountTransaction(@RequestBody OpenTransactionDto dto) {
        //TODO: e-commerce
        return new Transaction();
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_transaction_s')")
    @PostMapping("/admin/edit/money")
    public Transaction editMoneyToAccount(@RequestBody MoneyDto moneyDto) {
        Transaction newTransaction = new Transaction();
        newTransaction.setCurrency(moneyDto.getCurrency());
        newTransaction.setInnerFrom(true);
        newTransaction.setInnerTo(true);
        newTransaction.setMoney(moneyDto.getBalanceEdit());
        newTransaction.setOwnerUserId((int) AuthUtils.getUserId());
        newTransaction.setFromNumber("SYSTEM");
        newTransaction.setOwnerUsername(AuthUtils.getAuthUser().getName());
        newTransaction.setToNumber(moneyDto.getAccountNumber());
        return transactionService.createSystem(newTransaction);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/account/{number}")
    public Page<TransactionResponseDto> getTransactionList(Pageable pageable, @PathVariable String number) {
        return transactionService.getByUserIdAndNumber(((int) AuthUtils.getUserId()), number, pageable)
                .map(mapper::toDTO);
    }

}
