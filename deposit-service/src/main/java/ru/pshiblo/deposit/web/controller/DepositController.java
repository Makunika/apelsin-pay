package ru.pshiblo.deposit.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.deposit.web.dto.request.CreateDepositDto;
import ru.pshiblo.deposit.web.dto.response.DepositResponseDto;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.deposit.mappers.DepositMapper;
import ru.pshiblo.account.service.DepositService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/deposit")
public class DepositController {

    private final DepositService service;
    private final DepositMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public DepositResponseDto create(@RequestBody CreateDepositDto request) {
        return mapper.toDto(
                service.create(
                        request.getUserId(),
                        request.getDepositTypeId(),
                        request.getMonths())
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping
    public List<DepositResponseDto> getAllByUserId() {
        return service.getByUserId((int) AuthUtils.getUserId())
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("number/{number}")
    public DepositResponseDto getDepositByNumber(@PathVariable String number) {
        return mapper.toDto(
                service.getByNumber(number)
                        .orElseThrow(() -> new NotFoundException(number, Deposit.class))
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("number/{number}")
    public void blockDeposit(@PathVariable String number) {
        service.block(number, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_server')")
    @GetMapping("{id}")
    public DepositResponseDto getDepositById(@PathVariable Integer id) {
        return mapper.toDto(
                service.getById(id)
                        .orElseThrow(() -> new NotFoundException(id, Deposit.class))
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_server')")
    @DeleteMapping("{id}")
    public void blockDeposit(@PathVariable Integer id) {
        service.blockById(id);
    }
}
