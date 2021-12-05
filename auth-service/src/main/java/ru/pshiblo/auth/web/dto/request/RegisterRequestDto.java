package ru.pshiblo.auth.web.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

/**
 * @author Maxim Pshiblo
 */
@Data
public class RegisterRequestDto {
    @JsonProperty(required = true)
    private String login;
    @JsonProperty(required = true)
    private String password;
    @JsonProperty(required = true)
    private String firstName;
    @JsonProperty(required = true)
    private String lastName;
    private String middleName;
    @JsonProperty(required = true)
    private String passportNumber;
    @JsonProperty(required = true)
    private String passportSeries;
    @JsonProperty(required = true)
    private LocalDate birthday;
    @JsonProperty(required = true)
    private String email;
    @JsonProperty(required = true)
    private String phone;
}
