package ru.pshiblo.account.business.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.account.business.mappers.BusinessAccountTypeMapper;
import ru.pshiblo.account.business.services.BusinessAccountTypeService;
import ru.pshiblo.account.business.web.dto.request.CreateBusinessAccountTypeDto;
import ru.pshiblo.account.business.web.dto.response.BusinessAccountTypeResponseDto;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/business/type")
public class BusinessAccountTypeController {

    private final BusinessAccountTypeService service;
    private final BusinessAccountTypeMapper mapper;

    @PostMapping
    public BusinessAccountTypeResponseDto create(@Valid @RequestBody CreateBusinessAccountTypeDto request) {
        return mapper.toDTO(
                service.createType(
                        mapper.toEntity(request)
                )
        );
    }

}
