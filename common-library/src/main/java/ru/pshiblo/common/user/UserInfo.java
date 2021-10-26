package ru.pshiblo.common.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Maxim Pshiblo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfo extends SmallUserInfo {
    private String lastName;
    private String middleName;
}
