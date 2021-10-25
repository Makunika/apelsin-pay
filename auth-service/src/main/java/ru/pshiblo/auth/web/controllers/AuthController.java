package ru.pshiblo.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.auth.mappings.AuthTokensMapper;
import ru.pshiblo.auth.service.interfaces.AuthService;
import ru.pshiblo.auth.web.dto.request.LoginPasswordDto;
import ru.pshiblo.auth.web.dto.request.LoginRefreshTokenDto;
import ru.pshiblo.auth.web.dto.response.AuthTokensDto;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequestMapping("/login/")
@RequiredArgsConstructor
public class AuthController {

    private final AuthTokensMapper authTokensMapper;
    private final AuthService authService;

    @PostMapping("password")
    public AuthTokensDto authWithPassword(@RequestBody LoginPasswordDto loginPasswordDto) {
        return authTokensMapper.toDTO(authService.loginWithPassword(loginPasswordDto.getLogin(), loginPasswordDto.getPassword()));
    }

    @PostMapping("refresh-token")
    public AuthTokensDto authWithToken(@RequestBody LoginRefreshTokenDto loginRefreshTokenDto) {
        return authTokensMapper.toDTO(authService.loginWithRefreshToken(loginRefreshTokenDto.getRefresh()));
    }

}
