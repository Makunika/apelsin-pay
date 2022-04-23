package ru.pshiblo.account.personal.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.account.personal.core.services.PersonalAccountTypeService;
import ru.pshiblo.account.personal.mappers.PersonalAccountMapper;
import ru.pshiblo.account.personal.web.dto.request.CreatePersonalAccountTypeDto;
import ru.pshiblo.account.personal.web.dto.response.PersonalTypeResponseDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/type")
public class PersonalAccountTypeController {

    private final PersonalAccountTypeService service;
    private final PersonalAccountMapper mapper;

    @PreAuthorize("hasAuthority('SCOPE_user') && hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_MODERATOR')")
    @PostMapping
    public PersonalTypeResponseDto createType(@Valid @RequestBody CreatePersonalAccountTypeDto request) {
        return mapper.personalAccountTypeToPersonalTypeResponseDto(
                service.createType(
                        mapper.createPersonalAccountTypeDtoToPersonalAccountType(request)
                )
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("{id}")
    public PersonalTypeResponseDto getById(@PathVariable int id) {
        return mapper.personalAccountTypeToPersonalTypeResponseDto(
                service.getById(id).orElseThrow()
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user') && hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_MODERATOR')")
    @GetMapping
    public List<PersonalTypeResponseDto> getAll() {
        return service.getAll()
                .stream()
                .map(mapper::personalAccountTypeToPersonalTypeResponseDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCOPE_user') && hasAnyAuthority('ROLE_ADMINISTRATOR', 'ROLE_MODERATOR')")
    @DeleteMapping("{typeId}")
    public void blockType(@PathVariable int typeId) {
        service.blockType(typeId);
    }

}
