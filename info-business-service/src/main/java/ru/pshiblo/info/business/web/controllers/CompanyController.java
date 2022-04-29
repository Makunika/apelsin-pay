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
    public void create(@Valid @RequestBody CreateCompanyDto request) {
        service.create(
                mapper.createCompanyDtoToCompany(request),
                AuthUtils.getAuthUser()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PutMapping
    public void update(@Valid @RequestBody UpdateCompanyDto request) {
        service.update(
                mapper.updateCompanyDtoToCompany(request),
                AuthUtils.getAuthUser()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user') && hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_MODERATOR')")
    @PostMapping("/confirm/{id}")
    public void confirm(@PathVariable long id) {
        service.confirm(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_user') && hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_MODERATOR')")
    @PostMapping("/confirm/failed")
    public void failedConfirm(@Valid @RequestBody FailedConfirmCompany request) {
        service.failedConfirm(request.getId(), request.getReason());
    }

    @PreAuthorize("hasAuthority('SCOPE_info_b_s')")
    @PostMapping("/{companyId}/check/key")
    public ResponseEntity<Boolean> checkApiKey(@PathVariable long companyId, @RequestBody String apiKey) {
        boolean result = service.checkApiKey(companyId, apiKey);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/{companyId}/regenerate/key")
    public void regenerateApiKey(@PathVariable long companyId) {
        service.regenerateApiKry(companyId, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/{companyId}/key")
    public ResponseEntity<String> getApiKey(@PathVariable long companyId) {
        String apiKey = service.getApiKey(companyId, AuthUtils.getAuthUser());
        return ResponseEntity.ok(apiKey);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("{id}")
    public void delete(@PathVariable long id) {
        service.delete(id, AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/current")
    public List<CompanyUserResponseDto> findByUser() {
        return service.findByUser(AuthUtils.getUserId())
                .stream()
                .map(mapper::companyUserToCompanyUserResponseDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_info_b_s')")
    @GetMapping("/user/{id}")
    public List<CompanyUserResponseDto> findByUser(@PathVariable long id) {
        return service.findByUser(id)
                .stream()
                .map(mapper::companyUserToCompanyUserResponseDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/current/owner")
    public List<CompanyUserResponseDto> findOwnerCompanies() {
        return service.findOwnerCompanies(AuthUtils.getUserId())
                .stream()
                .map(mapper::companyUserToCompanyUserResponseDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("{id}")
    public CompanyResponseDto findById(@PathVariable long id) {
        return mapper.companyToCompanyResponseDto(
                service.findById(id).orElseThrow()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/user")
    public void addUserToCompany(@Valid @RequestBody AddUserToCompanyDto request) {
        service.addUserToCompany(
                request.getCompanyId(),
                AuthUtils.getAuthUser(),
                request.getUserId(),
                request.getRoleCompany()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("/user")
    public void deleteUserFromCompany(@Valid @RequestBody DeleteUserFromCompanyDto request) {
        service.deleteUserFromCompany(
                request.getCompanyId(),
                AuthUtils.getAuthUser(),
                request.getUserId()
        );
    }

}
