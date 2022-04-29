package ru.pshiblo.payment.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.payment.service.PayService;
import ru.pshiblo.payment.web.dto.request.PayInnerDto;
import ru.pshiblo.payment.web.dto.request.PayTinkoffDto;
import ru.pshiblo.payment.web.dto.response.PayInnerResponseDto;
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
    public PayInnerResponseDto payInner(@Valid @RequestBody PayInnerDto request) {
        PayInnerResponseDto payInnerResponseDto = new PayInnerResponseDto();
        payInnerResponseDto.setTransactionId(
                service.payInner(
                                request.getOrderId(),
                                AuthUtils.getAuthUser(),
                                request.getAccountNumberFrom()
                        )
                        .getTransactionId()
        );
        return payInnerResponseDto;
    }

    @PostMapping("/public/pay/tinkoff")
    public PayTinkoffResponseDto payTinkoff(@RequestBody PayTinkoffDto request) {
        String url = service.payTinkoff(request.getOrderId());
        return new PayTinkoffResponseDto(url);
    }
}
