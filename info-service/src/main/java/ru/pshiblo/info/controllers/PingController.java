package ru.pshiblo.info.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.model.AuthUser;

import java.security.Principal;

@RestController
public class PingController {

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/ping")
    public AuthUser get() {
        return AuthUtils.getAuthUser();
    }
}
