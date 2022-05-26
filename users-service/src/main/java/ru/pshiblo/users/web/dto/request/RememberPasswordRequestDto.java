package ru.pshiblo.users.web.dto.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RememberPasswordRequestDto {
    @NotNull
    @Email
    private String email;
}
