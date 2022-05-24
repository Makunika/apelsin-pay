package ru.pshiblo.info.personal.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.mappers.PersonInfoMapper;
import ru.pshiblo.info.personal.services.PersonInfoService;
import ru.pshiblo.info.personal.web.dto.*;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.annotation.IsAdmin;
import ru.pshiblo.security.annotation.IsModer;
import ru.pshiblo.security.annotation.IsUser;

import javax.validation.Valid;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/persons")
@Slf4j
public class PersonInfoController {
    private final PersonInfoService service;
    private final PersonInfoMapper mapper;

    @PutMapping("/{id}")
    @IsUser
    public void updateInfo(@Valid @RequestBody UpdatePersonInfoDto request, @PathVariable long id) {
        PersonInfo personInfo = mapper.toEntity(request, id);
        service.update(personInfo, AuthUtils.getUserId());
    }

    @GetMapping("all")
    @IsModer
    public Page<PersonInfoDto> getAll(Pageable pageable) {
        return service.findAll(pageable)
                .map(mapper::toDTO);
    }

    @GetMapping("all/nopage")
    @IsModer
    public List<PersonInfoDto> getAllNoPageable() {
        return service.findAll()
                .stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('SCOPE_info_p_s')")
    public PersonInfoDto findById(@PathVariable long id) {
        return mapper.toDTO(service.findById(id)
                .orElseThrow(() -> new NotFoundException(id, PersonInfo.class)));
    }

    @GetMapping("/user/{id}")
    @PreAuthorize("hasAuthority('SCOPE_info_p_s')")
    public PersonInfoDto findByUserId(@PathVariable long id) {
        return mapper.toDTO(
                service.findByUserId(id)
                        .filter(pi -> !pi.getIsLock())
                        .orElseThrow(() -> new NotFoundException(id, PersonInfo.class)));
    }

    @GetMapping
    @IsUser
    public PersonInfoDto getAuthPersonInfo() {
        return mapper.toDTO(service.findByUserId(AuthUtils.getUserId())
                .orElseThrow(() -> new NotFoundException(AuthUtils.getUserId(), PersonInfo.class)));
    }

    @GetMapping("/user/{id}/simple")
    @IsUser
    public SimplePersonInfoDto getNameById(@PathVariable long id) {
        return service.findByUserId(id)
                .filter(pi -> !pi.getIsLock())
                .map(mapper::toSimpleDTO)
                .orElseThrow(() -> new NotFoundException(id, PersonInfo.class));
    }

    @GetMapping("/user/search/username/{login}")
    @IsUser
    public List<SimplePersonInfoDto> searchByUsername(@PathVariable String login) {
        return service.findByLogin(login)
                .stream()
                .filter(pi -> !pi.getIsLock())
                .sorted(Comparator.comparing(PersonInfo::getLogin))
                .map(mapper::toSimpleDTO)
                .collect(Collectors.toList());
    }
}
