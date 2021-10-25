package ru.pshiblo.auth.web.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @author Maxim Pshiblo
 */
@Data
public class LoginRefreshTokenDto {
    @JsonProperty(required = true)
    private String refresh;
}
