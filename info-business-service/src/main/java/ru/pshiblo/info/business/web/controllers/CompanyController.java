package ru.pshiblo.info.business.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.info.business.mappers.CompanyMapper;
import ru.pshiblo.info.business.services.CompanyService;
import ru.pshiblo.info.business.web.dto.*;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.annotation.IsModer;
import ru.pshiblo.security.enums.ConfirmedStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/company")
public class CompanyController {
    private final CompanyService service;
    private final CompanyMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping
    public CompanyResponseDto create(@Valid @RequestBody CreateCompanyDto request) {
        return mapper.toDTO(
                service.create(
                        mapper.toEntity(request),
                        AuthUtils.getAuthUser()
                )
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PutMapping
    public CompanyResponseDto update(@Valid @RequestBody UpdateCompanyDto request) {
        return mapper.toDTO(
                service.update(
                        mapper.toEntity(request),
                        AuthUtils.getAuthUser()
                )
        );
    }

    @IsModer
    @GetMapping("/on-conf")
    public List<CompanyResponseDto> getOnConfirmed() {
        return service.findByStatus(ConfirmedStatus.ON_CONFIRMED)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.delete(id, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("{id}")
    public CompanyResponseDto findById(@PathVariable long id) {
        return mapper.toDTO(
                service.findById(id).orElseThrow()
        );
    }

}
