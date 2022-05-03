package ru.pshiblo.payment.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.payment.domain.Order;
import ru.pshiblo.payment.service.PayService;
import ru.pshiblo.payment.web.dto.request.PayInnerDto;
import ru.pshiblo.payment.web.dto.request.PayTinkoffDto;
import ru.pshiblo.payment.web.dto.response.PayResponseDto;
import ru.pshiblo.payment.web.dto.response.PayTinkoffResponseDto;
import ru.pshiblo.security.AuthUtils;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
public class PayController {
    private final PayService service;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/pay")
    public PayResponseDto payInner(@Valid @RequestBody PayInnerDto request) {
        PayResponseDto payResponseDto = new PayResponseDto();
        payResponseDto.setTransactionId(
                service.payInner(
                                request.getOrderId(),
                                AuthUtils.getAuthUser(),
                                request.getAccountNumberFrom()
                        )
                        .getTransactionId()
        );
        return payResponseDto;
    }

    @PostMapping("/public/pay/tinkoff")
    public PayTinkoffResponseDto payTinkoff(@RequestBody PayTinkoffDto request) {
        PayTinkoffResponseDto payResponseDto = new PayTinkoffResponseDto();
        Order order = service.payTinkoff(request.getOrderId());
        payResponseDto.setTransactionId(order.getTransactionId());
        payResponseDto.setUrl(order.getPayTinkoffUrl());
        return payResponseDto;
    }

    @PostMapping("/public/pay/tinkoff/redirect")
    public void payTinkoffRedirect(@RequestBody PayTinkoffDto request) {
        service.paySuccessByTinkoff(request.getOrderId());
    }
}
