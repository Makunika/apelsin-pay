package ru.pshiblo.common.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LargeUserInfo extends UserInfo {
    private String login;
    private List<String> roles;
}
