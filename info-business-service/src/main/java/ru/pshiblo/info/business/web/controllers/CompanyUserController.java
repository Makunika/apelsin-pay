package ru.pshiblo.info.business.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.info.business.mappers.CompanyMapper;
import ru.pshiblo.info.business.services.CompanyService;
import ru.pshiblo.info.business.services.CompanyUserService;
import ru.pshiblo.info.business.web.dto.AddUserToCompanyDto;
import ru.pshiblo.info.business.web.dto.CompanyUserResponseDto;
import ru.pshiblo.info.business.web.dto.DeleteUserFromCompanyDto;
import ru.pshiblo.security.AuthUtils;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyUserController {

    private final CompanyUserService service;
    private final CompanyService companyService;
    private final CompanyMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/current")
    public List<CompanyUserResponseDto> findByUser() {
        return service.findByUser(AuthUtils.getUserId())
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/user")
    public void addUserToCompany(@Valid @RequestBody AddUserToCompanyDto request) {
        service.addUserToCompany(
                companyService.findByIdOrThrow(request.getCompanyId()),
                AuthUtils.getAuthUser(),
                request.getUserId(),
                request.getRoleCompany()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @DeleteMapping("/user")
    public void deleteUserFromCompany(@Valid @RequestBody DeleteUserFromCompanyDto request) {
        service.deleteUserFromCompany(
                companyService.findByIdOrThrow(request.getCompanyId()),
                AuthUtils.getAuthUser(),
                request.getUserId()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("{id}/users")
    public List<CompanyUserResponseDto> getUsersCompany(@PathVariable long id) {
        return service.findUsersInCompany(
                        companyService.findByIdOrThrow(id),
                        AuthUtils.getAuthUser()
                )
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    //--------

    @PreAuthorize("hasAuthority('SCOPE_info_b_s')")
    @GetMapping("/search/user/{id}")
    public List<CompanyUserResponseDto> findByUser(@PathVariable long id) {
        return service.findByUser(id)
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }
}
