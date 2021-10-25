package ru.pshiblo.protocol.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Maxim Pshiblo
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserInfo extends SmallUserInfo {
    private String lastName;
    private String middleName;
}
