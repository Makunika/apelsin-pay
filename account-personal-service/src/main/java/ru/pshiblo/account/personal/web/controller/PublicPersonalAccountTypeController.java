package ru.pshiblo.account.personal.web.controller;

import com.thoughtworks.xstream.mapper.PackageAliasingMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.account.personal.core.services.PersonalAccountTypeService;
import ru.pshiblo.account.personal.mappers.PersonalAccountMapper;
import ru.pshiblo.account.personal.web.dto.response.PersonalTypeResponseDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/public/type")
@RequiredArgsConstructor
public class PublicPersonalAccountTypeController {

    private final PersonalAccountTypeService service;
    private final PersonalAccountMapper mapper;

    @GetMapping
    public List<PersonalTypeResponseDto> getAllValid() {
        return service.getAllValid()
                .stream()
                .map(mapper::personalAccountTypeToPersonalTypeResponseDto)
                .collect(Collectors.toList());
    }
}
