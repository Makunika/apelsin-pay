package ru.pshiblo.transaction.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.domain.Card;
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
    private final CardService cardService;
    private final AccountService accountService;

    public Builder builder() {
        return new Builder();
    }

    public class Builder {
        private Transaction transaction;
        private int cvc;
        private AuthUser authUser;

        private Builder() {
            transaction = new Transaction();
        }

        public Builder cvc(int cvc) {
            this.cvc = cvc;
            return this;
        }

        public Builder authUser(AuthUser authUser) {
            this.authUser = authUser;
            return this;
        }

        public Builder money(BigDecimal money) {
            transaction.setMoney(money);
            return this;
        }

        public Builder currency(Currency currency) {
            transaction.setCurrency(currency);
            return this;
        }

        public Builder fromCard(String cardNumber) {
            if (cvc == 0 && authUser == null) {
                throw new IllegalStateException("set authUser or cvc code");
            }
            Card card = cardService.getByNumber(cardNumber);
            if (authUser != null) {
                if (card.getUserId().longValue() != authUser.getId()) {
                    throw new NotAllowedOperationException();
                }
                transaction.setOwnerUsername(authUser.getName());
            } else {
                if (card.getCvc() != cvc) {
                    throw new NotAllowedOperationException();
                }
                transaction.setOwnerUsername("CVC");
            }
            transaction.setOwnerUserId(card.getUserId());
            transaction.setFromNumber(card.getAccount().getNumber());
            transaction.setFromCardNumber(cardNumber);
            transaction.setCurrencyFrom(card.getAccount().getCurrency());
            return this;
        }

        public Builder fromAccount(String accountNumber) {
            if (authUser == null) {
                throw new IllegalStateException("set authUser");
            }
            Account account = accountService.getByNumber(accountNumber);
            if (account.getUserId().longValue() != authUser.getId()) {
                throw new NotAllowedOperationException();
            }
            transaction.setOwnerUserId(account.getUserId());
            transaction.setOwnerUsername(authUser.getName());
            transaction.setFromNumber(account.getNumber());
            return this;
        }

        public Builder toAccount(String accountNumber) {
            transaction.setToNumber(accountNumber);
            accountService.findByNumber(accountNumber).ifPresentOrElse(
                    account -> {
                        transaction.setInner(true);
                        transaction.setToUserId(account.getUserId());
                    },
                    () -> {
                        transaction.setInner(false);
                    }
            );
            return this;
        }

        public Builder toCard(String cardNumber) {
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
