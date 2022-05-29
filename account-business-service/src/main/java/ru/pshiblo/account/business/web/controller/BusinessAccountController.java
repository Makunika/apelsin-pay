package ru.pshiblo.account.business.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.account.business.mappers.BusinessAccountMapper;
import ru.pshiblo.account.business.services.BusinessAccountService;
import ru.pshiblo.account.business.domain.BusinessAccountType;
import ru.pshiblo.account.business.services.BusinessAccountTypeService;
import ru.pshiblo.account.business.web.dto.request.ChangeTypeAccountRequestDto;
import ru.pshiblo.account.business.web.dto.request.CreateBusinessAccountDto;
import ru.pshiblo.account.business.web.dto.response.BusinessAccountResponseDto;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.security.AuthUtils;

import java.util.Collection;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/business")
public class BusinessAccountController {

    private final BusinessAccountService businessService;
    private final BusinessAccountTypeService businessTypeService;
    private final BusinessAccountMapper businessMapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public BusinessAccountResponseDto createNew(@RequestBody CreateBusinessAccountDto request) {
        request.setUserId(AuthUtils.getUserId());
        return businessMapper
                .toDto(
                        businessService.create(
                                request.getCompanyId(),
                                request.getUserId(),
                                businessTypeService.getById((int) request.getTypeId())
                                        .orElseThrow(() -> new NotFoundException(request.getTypeId(),
                                                BusinessAccountType.class))
                        )
                );
    }

    @PreAuthorize("hasAnyAuthority('SCOPE_user', 'SCOPE_account_b_s')")
    @GetMapping("/company/{companyId}")
    public BusinessAccountResponseDto getByCompanyId(@PathVariable Long companyId) {
        //TODO:CHECK COMPANY ID
        return businessMapper.toDto(businessService.getByCompanyId(companyId));
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/type/change")
    public void changeTypeOfAccount(@RequestBody ChangeTypeAccountRequestDto request) {
        businessService.changeTypeOfAccount(
                businessTypeService.getById(request.getTypeId())
                        .orElseThrow(() -> new NotFoundException(request.getTypeId(),
                                BusinessAccountType.class)),
                request.getNumber(),
                AuthUtils.getUserId());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("/block/{number}")
    public void block(@PathVariable String number) {
        businessService.block(number, AuthUtils.getUserId());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/unblock/{number}")
    public void unblock(@PathVariable String number) {
        businessService.unblock(number, AuthUtils.getUserId());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("{number}")
    public void delete(@PathVariable String number) {
        businessService.delete(number, AuthUtils.getUserId());
    }

}
