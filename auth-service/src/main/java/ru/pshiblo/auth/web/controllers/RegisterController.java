package ru.pshiblo.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.auth.mappings.UserMapper;
import ru.pshiblo.auth.service.interfaces.RegisterService;
import ru.pshiblo.auth.web.dto.request.ChangePasswordDto;
import ru.pshiblo.auth.web.dto.request.RegisterRequestDto;
import ru.pshiblo.auth.web.dto.response.RegisterResponseDto;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequestMapping("register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('server')")
    @PostMapping
    public RegisterResponseDto registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        return userMapper.toDTO(
                registerService.registerUser(
                        userMapper.toEntity(registerRequestDto),
                        registerRequestDto.getLogin(),
                        registerRequestDto.getPassword()
                )
        );
    }

    @PreAuthorize("hasAuthority('server')")
    @PutMapping("change-password")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        registerService.changePassword(changePasswordDto.getLogin(), changePasswordDto.getPassword(), changePasswordDto.getNewPassword());
    }
}
