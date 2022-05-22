package ru.pshiblo.account.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.service.CurrencyService;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final ObjectMapper objectMapper;
    private List<Rate> rates;

    @Scheduled(cron = "0 0 8 * * *")
    public void loadDailyRate() throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response =
                restTemplate.getForEntity("https://www.cbr-xml-daily.ru/daily_json.js", String.class);

        ObjectNode dailyRates = ((ObjectNode) objectMapper.readTree(response.getBody()));

        JsonNode valute = dailyRates.findValue("Valute");
        double usdToRub = valute.findValue("USD").findValue("Value").asDouble();
        double eurToRub = valute.findValue("EUR").findValue("Value").asDouble();

        rates = List.of(
                Rate.builder()
                        .from(Currency.RUB)
                        .to(Currency.USD)
                        .rate(BigDecimal.ONE.divide(BigDecimal.valueOf(usdToRub), 10, RoundingMode.CEILING))
                        .build(),
                Rate.builder()
                        .from(Currency.USD)
                        .to(Currency.RUB)
                        .rate(BigDecimal.valueOf(usdToRub))
                        .build(),
                Rate.builder()
                        .from(Currency.EUR)
                        .to(Currency.RUB)
                        .rate(BigDecimal.valueOf(eurToRub))
                        .build(),
                Rate.builder()
                        .from(Currency.RUB)
                        .to(Currency.EUR)
                        .rate(BigDecimal.ONE.divide(BigDecimal.valueOf(eurToRub), 10, RoundingMode.CEILING))
                        .build(),
                Rate.builder()
                        .from(Currency.USD)
                        .to(Currency.EUR)
                        .rate(BigDecimal.valueOf(usdToRub).divide(BigDecimal.valueOf(eurToRub), 10, RoundingMode.CEILING))
                        .build(),
                Rate.builder()
                        .from(Currency.EUR)
                        .to(Currency.USD)
                        .rate(BigDecimal.valueOf(eurToRub).divide(BigDecimal.valueOf(usdToRub), 10, RoundingMode.CEILING))
                        .build()
        );
    }

    @PostConstruct
    public void init() throws JsonProcessingException {
        loadDailyRate();
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
            return money.multiply(rate).setScale(2, RoundingMode.HALF_DOWN);
        }
    }
}
