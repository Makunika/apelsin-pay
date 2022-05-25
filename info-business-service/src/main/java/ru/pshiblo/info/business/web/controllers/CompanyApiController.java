package ru.pshiblo.info.business.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.info.business.services.CompanyApiService;
import ru.pshiblo.info.business.services.CompanyService;
import ru.pshiblo.security.AuthUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/company")
public class CompanyApiController {

    private final CompanyApiService service;
    private final CompanyService companyService;

    @PreAuthorize("hasAuthority('SCOPE_info_b_s')")
    @PostMapping("/{companyId}/api/check/key")
    public ResponseEntity<Boolean> checkApiKey(@PathVariable long companyId, @RequestBody String apiKey) {
        boolean result = service.checkApiKey(companyService.findByIdOrThrow(companyId), apiKey);
        return ResponseEntity.ok(result);
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PostMapping("/{companyId}/api/regenerate/key")
    public void regenerateApiKey(@PathVariable long companyId) {
        service.regenerateApiKey(companyService.findByIdOrThrow(companyId), AuthUtils.getAuthUser());
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/{companyId}/api/key")
    public ResponseEntity<String> getApiKey(@PathVariable long companyId) {
        String apiKey = service.getApiKey(companyService.findByIdOrThrow(companyId), AuthUtils.getAuthUser());
        return ResponseEntity.ok(apiKey);
    }
}
