package ru.pshiblo.users.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.users.service.RememberPasswordService;
import ru.pshiblo.users.web.dto.request.CreateNewPasswordByTokenRequestDto;
import ru.pshiblo.users.web.dto.request.RememberPasswordRequestDto;

import javax.validation.Valid;

@RestController
@RequestMapping("public/remember")
@RequiredArgsConstructor
public class RememberPasswordController {
    private final RememberPasswordService service;

    @PostMapping
    public void rememberRequest(@Valid @RequestBody RememberPasswordRequestDto request) {
        service.rememberPassword(request.getEmail());
    }

    @PostMapping("/new-password")
    public void createNewPassword(@Valid @RequestBody CreateNewPasswordByTokenRequestDto request) {
        service.changePasswordByToken(
                request.getToken(),
                request.getNewPassword()
        );
    }
}
