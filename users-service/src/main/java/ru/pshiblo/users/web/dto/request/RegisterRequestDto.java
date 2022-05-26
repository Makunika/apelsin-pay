package ru.pshiblo.users.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
    private String email;
}
