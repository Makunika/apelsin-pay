package ru.pshiblo.transaction.web.controllers.api;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.account.enums.Currency;
import ru.pshiblo.account.service.CurrencyService;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.annotation.IsUser;
import ru.pshiblo.transaction.model.InfoPrepare;
import ru.pshiblo.transaction.service.TransactionService;
import ru.pshiblo.transaction.web.dto.request.ConvertMoneyRequestDto;
import ru.pshiblo.transaction.web.dto.request.InfoPrepareRequestDto;
import ru.pshiblo.transaction.web.dto.response.ConvertMoneyResponseDto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("public/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService service;

    @PostMapping
    public ConvertMoneyResponseDto convertMoney(@RequestBody ConvertMoneyRequestDto rq) {
        BigDecimal convertMoney = service.convertMoney(rq.getCurrencyFrom(), rq.getCurrencyTo(), rq.getAmount());
        return new ConvertMoneyResponseDto(rq.getCurrencyTo(), convertMoney);
    }

    @GetMapping("/rate/by/rub")
    public List<ConvertMoneyResponseDto> getRateByRub() {
        List<ConvertMoneyResponseDto> result = new ArrayList<>();
        result.add(new ConvertMoneyResponseDto(
                Currency.USD,
                service.convertMoney(Currency.USD, Currency.RUB, BigDecimal.ONE)
        ));
        result.add(new ConvertMoneyResponseDto(
                Currency.EUR,
                service.convertMoney(Currency.EUR, Currency.RUB, BigDecimal.ONE)
        ));
        return result;
    }

}
