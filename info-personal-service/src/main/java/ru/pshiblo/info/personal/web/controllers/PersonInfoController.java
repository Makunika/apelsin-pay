package ru.pshiblo.info.personal.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.mappers.PersonInfoMapper;
import ru.pshiblo.info.personal.services.PersonInfoService;
import ru.pshiblo.info.personal.web.dto.ConfirmedPersonInfoDto;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;
import ru.pshiblo.info.personal.web.dto.RegisterPersonInfoDto;
import ru.pshiblo.security.AuthUtils;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/persons")
@Slf4j
public class PersonInfoController {
    private final PersonInfoService service;
    private final PersonInfoMapper mapper;

    @PostMapping("/conf")
    @PreAuthorize("hasAuthority('SCOPE_user')")
    public void toConfirmed(@Valid @RequestBody ConfirmedPersonInfoDto request) {
        service.confirmed(request.getPassportNumber(), request.getPassportSeries(), request.getPersonInfoId(), AuthUtils.getUserId());
    }

    @PostMapping("/conf/accept/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR', 'ROLE_ADMINISTRATOR')")
    public void confirmedAccept(@PathVariable long id) {
        service.confirmedAccept(id);
    }

    @PostMapping("/conf/fail/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR', 'ROLE_ADMINISTRATOR')")
    public void confirmedFailed(@PathVariable long id) {
        service.confirmedFailed(id);
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_info_p_s')")
    public PersonInfoDto findById(@PathVariable long id) {
        return mapper.personInfoToPersonInfoDto(service.findById(id)
                .orElseThrow(() -> new NotFoundException(id, PersonInfo.class)));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_info_p_s')")
    public PersonInfoDto findByUserId(@PathVariable long id) {
        return mapper.personInfoToPersonInfoDto(service.findByUserId(id)
                .orElseThrow(() -> new NotFoundException(id, PersonInfo.class)));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_user')")
    public PersonInfoDto getAuthPersonInfo() {
        return mapper.personInfoToPersonInfoDto(service.findByUserId(AuthUtils.getUserId())
                .orElseThrow(() -> new NotFoundException(AuthUtils.getUserId(), PersonInfo.class)));
    }

    @GetMapping("/onconf")
    @PreAuthorize("hasAnyAuthority('ROLE_MODERATOR', 'ROLE_ADMINISTRATOR')")
    public List<PersonInfoDto> findOnConfirmed() {
        return service.findByOnConfirmed().stream().map(mapper::personInfoToPersonInfoDto).collect(Collectors.toList());
    }
}
