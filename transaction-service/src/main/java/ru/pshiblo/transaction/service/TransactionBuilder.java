package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.Card;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.account.service.CardService;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.repository.TransactionRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionBuilder {

    private final TransactionRepository repository;
    private final AccountService accountService;

    public BuilderInner builderInner() {
        return new BuilderInner();
    }

    public class BuilderInner {
        private Transaction transaction;
        private int cvc;
        private AuthUser authUser;

        private BuilderInner() {
            transaction = new Transaction();
        }

        public BuilderInner authUser(AuthUser authUser) {
            this.authUser = authUser;
            return this;
        }

        public BuilderInner money(BigDecimal money) {
            transaction.setMoney(money);
            return this;
        }

        public BuilderInner currency(Currency currency) {
            transaction.setCurrency(currency);
            return this;
        }

        public BuilderInner fromAccount(String accountNumber) {
            if (authUser == null) {
                throw new IllegalStateException("set authUser");
            }
            Account account = accountService.getByNumber(accountNumber);
//            if (account.getType() == AccountType.PERSONAL) {
//
//            }
            transaction.setOwnerUserId((int) authUser.getId());
            transaction.setOwnerUsername(authUser.getName());
            transaction.setInnerFrom(true);
            transaction.setFromNumber(account.getNumber());
            return this;
        }

        public BuilderInner toAccount(String accountNumber) {
            transaction.setToNumber(accountNumber);
            accountService.findByNumber(accountNumber).ifPresentOrElse(
                    account -> {
                        transaction.setInnerTo(true);
                        transaction.setToUserId(account.getUserId());
                    },
                    () -> {
                        transaction.setInner(false);
                    }
            );
            return this;
        }

        public BuilderInner toCard(String cardNumber) {
            cardService.findByNumber(cardNumber).ifPresentOrElse(
                    card -> {
                        transaction.setToUserId(card.getUserId());
                        transaction.setInner(true);
                        transaction.setToNumber(card.getAccount().getNumber());
                        transaction.setToCardNumber(cardNumber);
                    },
                    () -> {
                        transaction.setInner(false);
                        transaction.setToCardNumber(cardNumber);
                    }
            );
            return this;
        }

        public Transaction build() {
            return transaction;
        }
    }
}
