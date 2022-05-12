package ru.pshiblo.info.personal.clients.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterAuthDto {
    private String login;
    private String password;
}
