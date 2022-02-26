package ru.pshiblo.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.auth.web.dto.response.UserDto;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/userinfo")
    public UserDto getUserInfo(@AuthenticationPrincipal Principal principal) {
        return new UserDto();
    }
}
