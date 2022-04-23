package ru.pshiblo.info.personal.clients.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterAuthDto {
    private String login;
    private String password;
}
