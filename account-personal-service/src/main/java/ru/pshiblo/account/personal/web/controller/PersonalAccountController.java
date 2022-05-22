package ru.pshiblo.account.personal.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.services.PersonalAccountTypeService;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;
import ru.pshiblo.account.personal.core.services.PersonalAccountService;
import ru.pshiblo.account.personal.mappers.PersonalAccountMapper;
import ru.pshiblo.account.personal.web.dto.request.CreateAccountPersonalDto;
import ru.pshiblo.account.personal.web.dto.response.PersonalResponseDto;
import ru.pshiblo.security.AuthUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/personal")
public class PersonalAccountController {

    private final PersonalAccountService service;
    private final PersonalAccountTypeService typeService;
    private final PersonalAccountMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public PersonalResponseDto create(@RequestBody CreateAccountPersonalDto request) {
        return mapper.toDto(
                service.create(
                        AuthUtils.getAuthUser(),
                        typeService.getById(request.getTypeId())
                                .orElseThrow(() -> new NotFoundException(request.getTypeId(), PersonalAccountType.class)))
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping
    public List<PersonalResponseDto> getAllByUserId() {
        return service.getByUserId((int) AuthUtils.getUserId())
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("number/{number}")
    public PersonalResponseDto getAccountByNumber(@PathVariable String number) {
        return mapper.toDto(
                service.getByNumber(number)
                        .filter(p -> p.getUserId().equals(AuthUtils.getUserId()) || AuthUtils.hasRole("ROLE_ADMINISTRATOR"))
                        .orElseThrow(() -> new NotFoundException(number, PersonalAccount.class))
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("number/{number}")
    public void deleteDeposit(@PathVariable String number) {
        service.delete(number, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("block/number/{number}")
    public void blockDeposit(@PathVariable String number) {
        service.block(number, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("unblock/number/{number}")
    public void unblockDeposit(@PathVariable String number) {
        service.unblock(number, AuthUtils.getAuthUser());
    }
}
