package ru.pshiblo.account.business.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.account.business.services.BusinessAccountService;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/app/business")
public class AppController {
    private final BusinessAccountService service;

    @PreAuthorize("hasAnyAuthority('SCOPE_account_b_s')")
    @PostMapping("/check/{userId}/{number}")
    public ResponseEntity<Boolean> checkPersonalAccount(@PathVariable long userId, @PathVariable String number) {
        boolean check = service.checkOwnerBusinessAccount(userId, number);
        return ResponseEntity.ok(check);
    }

}
