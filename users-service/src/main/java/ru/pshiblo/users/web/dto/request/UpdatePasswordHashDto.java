package ru.pshiblo.users.web.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Уже закодированный хеш пароля (с префиксом {id} DelegatingPasswordEncoder, например {bcrypt}).
 */
@Data
public class UpdatePasswordHashDto {
    @NotBlank
    private String passwordHash;
}
