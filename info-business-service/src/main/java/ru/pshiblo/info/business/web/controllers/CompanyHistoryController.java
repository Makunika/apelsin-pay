package ru.pshiblo.info.business.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.info.business.mappers.CompanyMapper;
import ru.pshiblo.info.business.services.CompanyHistoryService;
import ru.pshiblo.info.business.web.dto.CompanyHistoryDto;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/history")
public class CompanyHistoryController {

    private final CompanyHistoryService service;
    private final CompanyMapper mapper;

    @GetMapping("{id}")
    public List<CompanyHistoryDto> getByCompanyId(@PathVariable long id) {
        return service.getByCompanyId(id)
                .stream()
                .map(mapper::companyHistoryToCompanyHistoryDto)
                .collect(Collectors.toList());
    }

}
