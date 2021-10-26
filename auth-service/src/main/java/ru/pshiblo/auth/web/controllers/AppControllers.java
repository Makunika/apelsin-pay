package ru.pshiblo.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.token.TokenService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.auth.service.interfaces.AuthService;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequestMapping("app")
@RequiredArgsConstructor
public class AppControllers {

    private final AuthService authService;

    @PostMapping("/token/check")
    public Boolean checkJwtToken(@RequestBody String token) {
        return authService.checkJwtTokenInDb(token);
    }
}
