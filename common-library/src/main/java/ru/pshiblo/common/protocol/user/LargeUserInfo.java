package ru.pshiblo.common.protocol.user;

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
