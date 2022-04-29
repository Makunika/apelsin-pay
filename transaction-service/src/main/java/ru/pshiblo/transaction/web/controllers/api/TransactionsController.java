package ru.pshiblo.transaction.web.controllers.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.model.PayoutModel;
import ru.pshiblo.transaction.service.TransactionBuilder;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.web.dto.request.MoneyDto;
import ru.pshiblo.transaction.web.dto.request.OpenPaymentInnerDto;
import ru.pshiblo.transaction.web.dto.request.OpenTransactionDto;
import ru.pshiblo.transaction.web.dto.request.OpenTransactionExternalToDto;

import javax.validation.Valid;

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

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/from/account/to/external")
    public Transaction openFromAccountToExternalTransaction(@RequestBody OpenTransactionExternalToDto dto) {
        PayoutModel payoutModel = dto.getPayoutModel();
        if (!payoutModel.getIsPerson()) {
            Assert.notNull(payoutModel.getKpp(), "kpp is null");
        } else {
            payoutModel.setInn("0");
        }
        Transaction transaction = transactionBuilder.builderOutTo()
                .authUser(AuthUtils.getAuthUser())
                .money(dto.getMoney())
                .currency(Currency.RUB)
                .fromAccount(dto.getFromNumber())
                .to(payoutModel)
                .build();
        return transactionService.create(transaction);
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

}
