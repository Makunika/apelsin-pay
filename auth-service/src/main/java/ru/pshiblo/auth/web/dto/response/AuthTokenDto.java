package ru.pshiblo.auth.web.dto.response;

import lombok.Data;

/**
 *
 * @author Maxim Pshiblo
 */
@Data
public class AuthTokenDto {
    private String jwt;
}