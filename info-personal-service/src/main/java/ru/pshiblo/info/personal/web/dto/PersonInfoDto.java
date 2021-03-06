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
    private Long id;
    @NotNull
    private Long userId;
    @NotNull
    private String firstName;
    @NotNull
    @NotBlank
    private String lastName;
    private String login;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    private String phone;
    @NotNull
    private LocalDate birthday;
    @NotNull
    private String passportSeries;
    @NotNull
    private String passportNumber;
    @NotNull
    private PersonStatus status;
    private Boolean lock;
}
