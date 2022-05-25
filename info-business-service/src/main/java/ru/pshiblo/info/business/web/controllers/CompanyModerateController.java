package ru.pshiblo.info.business.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.info.business.services.CompanyModerateService;
import ru.pshiblo.info.business.services.CompanyService;
import ru.pshiblo.info.business.web.dto.CompanyResponseDto;
import ru.pshiblo.info.business.web.dto.FailedConfirmCompany;
import ru.pshiblo.security.annotation.IsModer;
import ru.pshiblo.security.enums.ConfirmedStatus;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company/moderate")
public class CompanyModerateController {

    private final CompanyModerateService service;
    private final CompanyService companyService;

    @IsModer
    @PostMapping("/confirm/accept/{id}")
    public void acceptConfirm(@PathVariable long id) {
        service.acceptConfirm(companyService.findByIdOrThrow(id));
    }

    @IsModer
    @PostMapping("/confirm/failed/{id}")
    public void failedConfirm(@Valid @RequestBody FailedConfirmCompany request, @PathVariable long id) {
        service.failedConfirm(companyService.findByIdOrThrow(id), request.getReason());
    }
}
