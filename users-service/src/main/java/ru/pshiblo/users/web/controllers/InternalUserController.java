package ru.pshiblo.users.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.users.service.RegisterService;
import ru.pshiblo.users.web.dto.request.UpdatePasswordHashDto;

import javax.validation.Valid;

/**
 * Внутренние операции, доступные только сервисам (scope {@code server}).
 * Используется auth-service для прозрачного перехеширования legacy Hmac-паролей в bcrypt
 * после успешного входа.
 */
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('SCOPE_server')")
public class InternalUserController {

    private final RegisterService registerService;

    @PutMapping("/{id}/password-hash")
    public void updatePasswordHash(@PathVariable int id, @Valid @RequestBody UpdatePasswordHashDto request) {
        registerService.updatePasswordHash(id, request.getPasswordHash());
    }
}
