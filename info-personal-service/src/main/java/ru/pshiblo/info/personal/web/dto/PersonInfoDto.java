package ru.pshiblo.info.personal.web.dto;

import lombok.Data;
import ru.pshiblo.info.personal.enums.PersonStatus;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PersonInfoDto implements Serializable {
    @NotNull
    private final Long id;
    @NotNull
    private final Long userId;
    @NotNull
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
    private final Integer passportSeries;
    @NotNull
    private final Integer passportNumber;
    @NotNull
    private final PersonStatus status;
}
