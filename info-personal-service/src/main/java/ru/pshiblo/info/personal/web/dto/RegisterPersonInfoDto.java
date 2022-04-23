package ru.pshiblo.info.personal.web.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class RegisterPersonInfoDto implements Serializable {
    @NotNull
    @NotBlank
    private final String firstName;
    @NotNull
    @NotBlank
    private final String lastName;
    @NotNull
    @NotBlank
    private final String middleName;
    @NotNull
    @NotBlank
    @Email
    private final String email;
    @NotNull
    @NotBlank
    private final String phone;
    @NotNull
    private final LocalDate birthday;
    @NotNull
    @NotBlank
    private final String login;
    @NotNull
    @NotBlank
    private final String password;
}
