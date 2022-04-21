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
}
