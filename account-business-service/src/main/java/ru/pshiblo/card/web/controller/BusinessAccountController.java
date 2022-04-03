package ru.pshiblo.card.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.card.domain.BusinessAccountType;
import ru.pshiblo.card.mappers.BusinessAccountMapper;
import ru.pshiblo.card.services.BusinessAccountService;
import ru.pshiblo.card.services.BusinessAccountTypeService;
import ru.pshiblo.card.web.dto.request.CreateBusinessAccountDto;
import ru.pshiblo.card.web.dto.response.BusinessAccountResponseDto;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.security.AuthUtils;

import java.util.Collection;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/card")
public class BusinessAccountController {

    private final BusinessAccountService businessService;
    private final BusinessAccountTypeService businessTypeService;
    private final BusinessAccountMapper businessMapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public BusinessAccountResponseDto createNew(@RequestBody CreateBusinessAccountDto createBusinessAccountDto) {
        createBusinessAccountDto.setUserId(AuthUtils.getUserId());
        return businessMapper
                .toDto(
                        businessService.create(
                                createBusinessAccountDto.getCompanyId(),
                                createBusinessAccountDto.getUserId(),
                                businessTypeService.getById((int) createBusinessAccountDto.getTypeId())
                                        .orElseThrow(() -> new NotFoundException(createBusinessAccountDto.getTypeId(),
                                                BusinessAccountType.class))
                        )
                );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/company/{companyId}")
    public Collection<BusinessAccountResponseDto> getByCompanyId(@PathVariable Long companyId) {
        //TODO:CHECK COMPANY ID
        return businessMapper.toDtos(businessService.getByCompanyId(companyId));
    }

    @PreAuthorize("hasAuthority('SCOPE_server')")
    @GetMapping("{id}")
    public BusinessAccountResponseDto getCardById(@PathVariable Integer id) {
        //return businessMapper.toDto(businessService.get(companyId));
        return null;
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("{id}")
    public void block(@PathVariable Long id) {

    }

}
