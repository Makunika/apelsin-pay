package ru.pshiblo.auth.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Maxim Pshiblo
 */
@Data
public class LoginPasswordDto {
    @JsonProperty(required = true)
    private String login;
    @JsonProperty(required = true)
    private String password;
}
