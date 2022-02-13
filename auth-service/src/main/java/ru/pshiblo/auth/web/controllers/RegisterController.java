package ru.pshiblo.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
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

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping()
    public String ping() {
        return "ping";
    }

    @PreAuthorize("hasAuthority('SCOPE_server')")
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

    @PreAuthorize("hasAuthority('SCOPE_server')")
    @PutMapping("change-password")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        registerService.changePassword(changePasswordDto.getLogin(), changePasswordDto.getPassword(), changePasswordDto.getNewPassword());
    }
}
