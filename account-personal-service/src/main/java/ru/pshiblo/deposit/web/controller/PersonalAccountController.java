package ru.pshiblo.deposit.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.deposit.core.domain.PersonalAccount;
import ru.pshiblo.deposit.core.domain.PersonalAccountType;
import ru.pshiblo.deposit.core.services.PersonalAccountService;
import ru.pshiblo.deposit.core.services.PersonalAccountTypeService;
import ru.pshiblo.deposit.mappers.PersonalAccountMapper;
import ru.pshiblo.deposit.web.dto.request.CreateDepositDto;
import ru.pshiblo.deposit.web.dto.response.PersonalResponseDto;
import ru.pshiblo.security.AuthUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/deposit")
public class PersonalAccountController {

    private final PersonalAccountService service;
    private final PersonalAccountTypeService typeService;
    private final PersonalAccountMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public PersonalResponseDto create(@RequestBody CreateDepositDto request) {
        return mapper.toDto(
                service.create(
                        request.getUserId(),
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
    public PersonalResponseDto getDepositByNumber(@PathVariable String number) {
        return mapper.toDto(
                service.getByNumber(number)
                        .orElseThrow(() -> new NotFoundException(number, PersonalAccount.class))
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("number/{number}")
    public void blockDeposit(@PathVariable String number) {
        //service.block(number, AuthUtils.getAuthUser());
    }

//    @PreAuthorize("hasAuthority('SCOPE_server')")
//    @GetMapping("{id}")
//    public PersonalResponseDto getDepositById(@PathVariable Integer id) {
//        return mapper.toDto(
//                service.getById(id)
//                        .orElseThrow(() -> new NotFoundException(id, Deposit.class))
//        );
//    }

//    @PreAuthorize("hasAuthority('SCOPE_server')")
//    @DeleteMapping("{id}")
//    public void blockDeposit(@PathVariable Integer id) {
//        service.blockById(id);
//    }
}
