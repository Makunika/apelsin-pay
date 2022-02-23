package ru.pshiblo.info.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class PingController {

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/ping")
    public Principal get(@AuthenticationPrincipal Principal principal) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal1 = authentication.getPrincipal();
        return principal;
    }
}
