package ru.pshiblo.transaction.tinkoff.builders;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.pshiblo.transaction.tinkoff.model.request.*;

import java.math.BigDecimal;

@Service
public class TinkoffPaymentBuilder {

    @Value("${apelsin.account.number}")
    private String myAccountNumber;

    public Builder builder() {
        return new Builder();
    }

    public class Builder {
        private TinkoffTo to;
        private BigDecimal amount;
        private Long id;

        private Builder() {}

        public Builder toPersonal(TinkoffTo toPerson) {
            Assert.notNull(toPerson.getName(), "Name");
            Assert.isNull(toPerson.getKpp(), "Kpp");
            Assert.notNull(toPerson.getBik(), "Bik");
            Assert.notNull(toPerson.getBankName(), "BankName");
            Assert.notNull(toPerson.getCorrAccountNumber(), "CorrAccountNumber");
            Assert.notNull(toPerson.getAccountNumber(), "AccountNumber");
            toPerson.setInn("0");
            to = toPerson;
            return this;
        }

        public Builder toBusiness(TinkoffTo toBusiness) {
            Assert.notNull(toBusiness.getName(), "Name");
            Assert.notNull(toBusiness.getInn(), "Inn");
            Assert.notNull(toBusiness.getKpp(), "Kpp");
            Assert.notNull(toBusiness.getBik(), "Bik");
            Assert.notNull(toBusiness.getBankName(), "BankName");
            Assert.notNull(toBusiness.getCorrAccountNumber(), "CorrAccountNumber");
            Assert.notNull(toBusiness.getAccountNumber(), "AccountNumber");
            to = toBusiness;
            return this;
        }

        public Builder amountRubles(BigDecimal rubles) {
            amount = rubles;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public TinkoffPayment build() {
            Assert.notNull(to, "to");
            Assert.notNull(amount, "amount");
            Assert.notNull(id, "id");

            TinkoffPayment payment = new TinkoffPayment();
            payment.setAmount(amount);
            payment.setTo(to);
            payment.setPurpose("Вывод средств с Apelsin");
            payment.setId(id.toString());

            TinkoffFrom from = new TinkoffFrom();
            from.setAccountNumber(myAccountNumber);
            payment.setFrom(from);

            return payment;
        }
    }
}
