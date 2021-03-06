package ru.pshiblo.users.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.users.mappings.UserMapper;
import ru.pshiblo.users.service.RegisterService;
import ru.pshiblo.users.web.dto.request.ChangePasswordDto;
import ru.pshiblo.users.web.dto.request.RegisterRequestDto;
import ru.pshiblo.users.web.dto.response.RegisterResponseDto;

/**
 * @author Maxim Pshiblo
 */
@RestController
@RequestMapping("register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterService registerService;
    private final UserMapper userMapper;

    @PreAuthorize("hasAuthority('SCOPE_users_s')")
    @PostMapping
    public RegisterResponseDto registerUser(@RequestBody RegisterRequestDto registerRequestDto) {
        return userMapper.toDTO(
                registerService.registerUser(
                        registerRequestDto.getLogin(),
                        registerRequestDto.getPassword(),
                        registerRequestDto.getEmail()
                )
        );
    }

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @PutMapping("change-password")
    public void changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        registerService.changePassword(AuthUtils.getUserId(), changePasswordDto.getPassword(), changePasswordDto.getNewPassword());
    }
}
