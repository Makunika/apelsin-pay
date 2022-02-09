package ru.pshiblo.auth.service.interfaces;

import org.springframework.security.core.userdetails.UserDetails;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.common.protocol.user.LargeUserInfo;
import ru.pshiblo.common.protocol.user.SmallUserInfo;
import ru.pshiblo.common.protocol.user.UserInfo;

/**
 * @author Maxim Pshiblo
 */
public interface UserService {
    LargeUserInfo getLargeUserInfo(int userId);
    UserInfo getUserInfo(int userId);
    SmallUserInfo getSmallUserInfo(int userId);
    String getPassport(int userId);
    User getUserById(int userId);
    User getByUsername(String username);
}
