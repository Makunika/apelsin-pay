package ru.pshiblo.info.personal.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.info.personal.mappers.PersonInfoMapper;
import ru.pshiblo.info.personal.services.PersonInfoModerateService;
import ru.pshiblo.info.personal.services.PersonInfoService;
import ru.pshiblo.info.personal.web.dto.ConfirmedPersonInfoDto;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.annotation.IsModer;
import ru.pshiblo.security.annotation.IsUser;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/persons")
@Slf4j
public class PersonInfoModerateController {

    private final PersonInfoModerateService service;
    private final PersonInfoMapper mapper;

    @PostMapping("/conf")
    @IsUser
    public void toConfirmed(@Valid @RequestBody ConfirmedPersonInfoDto request) {
        service.confirmed(request.getPassportNumber(), request.getPassportSeries(), request.getPersonInfoId(), AuthUtils.getUserId());
    }

    @PostMapping("/conf/accept/{id}")
    @IsModer
    public void confirmedAccept(@PathVariable long id) {
        service.confirmedAccept(id);
    }

    @PostMapping("/conf/fail/{id}")
    @IsModer
    public void confirmedFailed(@PathVariable long id) {
        service.confirmedFailed(id);
    }

    @PostMapping("/ban/{id}")
    @IsModer
    public void banPerson(@PathVariable long id) {
        service.ban(id);
    }

    @PostMapping("/unban/{id}")
    @IsModer
    public void unbanPerson(@PathVariable long id) {
        service.unban(id);
    }

    @GetMapping("/onconf")
    @IsModer
    public List<PersonInfoDto> findOnConfirmed() {
        return service.findByOnConfirmed().stream().map(mapper::toDTO).collect(Collectors.toList());
    }
}
