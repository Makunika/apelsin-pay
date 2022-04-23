package ru.pshiblo.users.web.dto.request;

import lombok.Data;

/**
 * @author Maxim Pshiblo
 */
@Data
public class ChangePasswordDto {
    private String login;
    private String newPassword;
    private String password;
}
