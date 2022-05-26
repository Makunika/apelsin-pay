package ru.pshiblo.users.web.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CreateNewPasswordByTokenRequestDto {
    @NotBlank
    private String token;
    @NotBlank
    private String newPassword;
}
