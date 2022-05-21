package ru.pshiblo.account.business.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.account.business.domain.BusinessAccount;
import ru.pshiblo.account.business.mappers.BusinessAccountMapper;
import ru.pshiblo.account.business.services.BusinessAccountService;
import ru.pshiblo.account.business.web.dto.response.BusinessAccountResponseDto;
import ru.pshiblo.common.exception.NotFoundException;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/app/business")
public class AppController {
    private final BusinessAccountService service;
    private final BusinessAccountMapper mapper;

    @PreAuthorize("hasAnyAuthority('SCOPE_account_b_s')")
    @PostMapping("/check/{userId}/{number}")
    public ResponseEntity<Boolean> checkPersonalAccount(@PathVariable long userId, @PathVariable String number) {
        boolean check = service.checkOwnerBusinessAccount(userId, number);
        return ResponseEntity.ok(check);
    }

    @PreAuthorize("hasAuthority('SCOPE_account_b_s')")
    @GetMapping("/number/{number}")
    public BusinessAccountResponseDto getAccountByNumber(@PathVariable String number) {
        return mapper.toDto(
                service.getByNumber(number)
                        .orElseThrow(() -> new NotFoundException(number, BusinessAccount.class))
        );
    }

}
