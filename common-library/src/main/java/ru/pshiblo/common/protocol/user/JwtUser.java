package ru.pshiblo.common.protocol.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtUser {
    private int id;
    private String username;
    private String email;
    private List<String> roles;
}
