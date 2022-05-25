package ru.pshiblo.transaction.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.account.domain.Account;
import ru.pshiblo.account.enums.AccountType;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.service.AccountService;
import ru.pshiblo.common.exception.InternalException;
import ru.pshiblo.common.exception.NotAllowedOperationException;
import ru.pshiblo.security.model.AuthUser;
import ru.pshiblo.transaction.clients.BusinessAccountClient;
import ru.pshiblo.transaction.clients.PersonalAccountClient;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.enums.TransactionType;
import ru.pshiblo.transaction.model.PayoutModel;

import javax.validation.Valid;
import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class TransactionBuilder {

    private final AccountService accountService;
    private final BusinessAccountClient businessAccountClient;
    private final PersonalAccountClient personalAccountClient;
    private final ObjectMapper objectMapper;

    public BuilderInner builderInner() {
        return new BuilderInner();
    }

    public BuilderOutTo builderOutTo() {
        return new BuilderOutTo();
    }

    public BuilderOutFrom builderOutFrom() {
        return new BuilderOutFrom();
    }

    public class BuilderInner {
        private final Transaction transaction;
        private Long userId;
        private String username;

        private BuilderInner() {
            transaction = new Transaction();
            transaction.setType(TransactionType.TRANSFER);
        }

        public BuilderInner authUser(AuthUser authUser) {
            this.userId = authUser.getId();
            this.username = authUser.getName();
            return this;
        }

        public BuilderInner userId(long userId) {
            this.userId = userId;
            this.username = "NOT";
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

        public BuilderInner type(TransactionType type) {
            transaction.setType(type);
            return this;
        }

        public BuilderInner fromAccount(String accountNumber) {
            if (userId == null) {
                throw new IllegalStateException("set authUser");
            }
            Account account = accountService.getByNumber(accountNumber);
            if (account.getType() == AccountType.PERSONAL) {
                if (Boolean.FALSE.equals(personalAccountClient.checkPersonalAccount(userId, accountNumber).getBody())) {
                    throw new NotAllowedOperationException();
                }
            } else {
                if (Boolean.FALSE.equals(businessAccountClient.checkBusinessAccount(userId, accountNumber).getBody())) {
                    throw new NotAllowedOperationException();
                }
            }

            transaction.setOwnerUserId(userId.intValue());
            transaction.setOwnerUsername(username);
            transaction.setInnerFrom(true);
            transaction.setFromNumber(account.getNumber());
            return this;
        }

        public BuilderInner toAccount(String accountNumber) {
            transaction.setToNumber(accountNumber);
            accountService.findByNumber(accountNumber).orElseThrow();
            transaction.setInnerTo(true);
            return this;
        }

        public Transaction build() {
            return transaction;
        }
    }

    public class BuilderOutTo {
        private final Transaction transaction;
        private AuthUser authUser;

        private BuilderOutTo() {
            transaction = new Transaction();
            transaction.setType(TransactionType.TRANSFER);
        }

        public BuilderOutTo authUser(AuthUser authUser) {
            this.authUser = authUser;
            return this;
        }

        public BuilderOutTo money(BigDecimal money) {
            transaction.setMoney(money);
            return this;
        }

        public BuilderOutTo currency(Currency currency) {
            transaction.setCurrency(currency);
            return this;
        }

        public BuilderOutTo fromAccount(String accountNumber) {
            if (authUser == null) {
                throw new IllegalStateException("set authUser");
            }
            Account account = accountService.getByNumber(accountNumber);
            if (account.getType() == AccountType.PERSONAL) {
                if (Boolean.FALSE.equals(personalAccountClient.checkPersonalAccount(authUser.getId(), accountNumber).getBody())) {
                    throw new NotAllowedOperationException();
                }
            } else {
                if (Boolean.FALSE.equals(businessAccountClient.checkBusinessAccount(authUser.getId(), accountNumber).getBody())) {
                    throw new NotAllowedOperationException();
                }
            }

            transaction.setOwnerUserId((int) authUser.getId());
            transaction.setOwnerUsername(authUser.getName());
            transaction.setInnerFrom(true);
            transaction.setFromNumber(account.getNumber());
            return this;
        }

        public BuilderOutTo to(@Valid PayoutModel payoutModel) {
            try {
                payoutModel.setName("Апельсин pay");
                transaction.setAdditionInfoTo(objectMapper.writeValueAsString(payoutModel));
                transaction.setInnerTo(false);
                return this;
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
                throw new InternalException(e.getMessage());
            }
        }

        public Transaction build() {
            return transaction;
        }
    }

    public class BuilderOutFrom {
        private final Transaction transaction;
        private Long userId;
        private String username;

        private BuilderOutFrom() {
            transaction = new Transaction();
            transaction.setType(TransactionType.TRANSFER);
        }

        public BuilderOutFrom authUser(AuthUser authUser) {
            this.userId = authUser.getId();
            this.username = authUser.getName();
            return this;
        }

        public BuilderOutFrom userId(long userId) {
            this.userId = userId;
            this.username = "NOT";
            return this;
        }

        public BuilderOutFrom money(BigDecimal money) {
            transaction.setMoney(money);
            return this;
        }

        public BuilderOutFrom currency(Currency currency) {
            transaction.setCurrency(currency);
            return this;
        }

        public BuilderOutFrom toAccount(String accountNumber) {
            transaction.setToNumber(accountNumber);
            accountService.findByNumber(accountNumber).orElseThrow();
            transaction.setInnerTo(true);
            return this;
        }

        public BuilderOutFrom type(TransactionType type) {
            transaction.setType(type);
            return this;
        }

        public Transaction build() {
            transaction.setInnerFrom(false);
            transaction.setOwnerUsername(username);
            transaction.setOwnerUserId(Math.toIntExact(userId));
            return transaction;
        }
    }

}
