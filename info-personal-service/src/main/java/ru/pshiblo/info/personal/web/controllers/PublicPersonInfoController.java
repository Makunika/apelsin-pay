package ru.pshiblo.info.personal.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.info.personal.mappers.PersonInfoMapper;
import ru.pshiblo.info.personal.services.PersonInfoService;
import ru.pshiblo.info.personal.web.dto.RegisterPersonInfoDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public")
@Slf4j
public class PublicPersonInfoController {
    private final PersonInfoService service;
    private final PersonInfoMapper mapper;

    @PostMapping("/register")
    public void register(@Valid @RequestBody RegisterPersonInfoDto request) {
        service.register(mapper.toEntity(request), request.getPassword(), request.getLogin());
    }
}
