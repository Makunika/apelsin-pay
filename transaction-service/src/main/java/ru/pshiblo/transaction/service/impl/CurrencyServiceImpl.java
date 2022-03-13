package ru.pshiblo.transaction.service.impl;

import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.transaction.enums.Currency;
import ru.pshiblo.transaction.service.CurrencyService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final List<Rate> rates;

    {
        rates = List.of(
                Rate.builder()
                        .from(Currency.RUB)
                        .to(Currency.USD)
                        .rate(BigDecimal.ONE.divide(BigDecimal.valueOf(116.1), 10, RoundingMode.CEILING))
                        .build(),
                Rate.builder()
                        .from(Currency.USD)
                        .to(Currency.RUB)
                        .rate(BigDecimal.valueOf(116.1))
                        .build(),
                Rate.builder()
                        .from(Currency.EUR)
                        .to(Currency.RUB)
                        .rate(BigDecimal.valueOf(150.1))
                        .build(),
                Rate.builder()
                        .from(Currency.RUB)
                        .to(Currency.EUR)
                        .rate(BigDecimal.ONE.divide(BigDecimal.valueOf(150.1), 10, RoundingMode.CEILING))
                        .build(),
                Rate.builder()
                        .from(Currency.USD)
                        .to(Currency.EUR)
                        .rate(BigDecimal.ONE.divide(BigDecimal.valueOf(1.2), 10, RoundingMode.CEILING))
                        .build(),
                Rate.builder()
                        .from(Currency.EUR)
                        .to(Currency.USD)
                        .rate(BigDecimal.valueOf(1.2))
                        .build()
        );
    }

    @Override
    public BigDecimal convertMoney(Currency from, Currency to, BigDecimal money) {
        if (from == to) {
            return money;
        }
        return rates.stream()
                .filter(r -> r.getFrom() == from && r.getTo() == to)
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException(from.name() + "->" + to.name(), Currency.class)
                )
                .calculate(money);
    }

    @Data
    @Builder
    static class Rate {
        private Currency from;
        private Currency to;
        private BigDecimal rate;

        public BigDecimal calculate(BigDecimal money) {
            return money.multiply(rate).setScale(2, RoundingMode.CEILING);
        }
    }
}
