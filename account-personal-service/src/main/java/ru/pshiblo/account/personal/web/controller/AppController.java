package ru.pshiblo.account.personal.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.services.PersonalAccountTypeService;
import ru.pshiblo.account.personal.mappers.PersonalAccountMapper;
import ru.pshiblo.account.personal.core.services.PersonalAccountService;
import ru.pshiblo.account.personal.web.dto.response.PersonalResponseDto;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.security.AuthUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/app/personal")
public class AppController {
    private final PersonalAccountService service;
    private final PersonalAccountTypeService typeService;
    private final PersonalAccountMapper mapper;

    @PreAuthorize("hasAnyAuthority('SCOPE_account_p_s')")
    @PostMapping("/check/{userId}/{number}")
    public ResponseEntity<Boolean> checkPersonalAccount(@PathVariable long userId, @PathVariable String number) {
        boolean check = service.checkOwnerPersonalAccount(userId, number);
        return ResponseEntity.ok(check);
    }

    @PreAuthorize("hasAuthority('SCOPE_account_p_s')")
    @GetMapping("/number/{number}")
    public PersonalResponseDto getAccountByNumber(@PathVariable String number) {
        return mapper.toDto(
                service.getByNumber(number)
                        .orElseThrow(() -> new NotFoundException(number, PersonalAccount.class))
        );
    }

}
