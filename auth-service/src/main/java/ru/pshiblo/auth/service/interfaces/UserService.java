package ru.pshiblo.auth.service.interfaces;

import ru.pshiblo.auth.domain.User;
import ru.pshiblo.common.user.LargeUserInfo;
import ru.pshiblo.common.user.SmallUserInfo;
import ru.pshiblo.common.user.UserInfo;

/**
 * @author Maxim Pshiblo
 */
public interface UserService {
    LargeUserInfo getLargeUserInfo(int userId);
    UserInfo getUserInfo(int userId);
    SmallUserInfo getSmallUserInfo(int userId);
    String getPassport(int userId);
    User getUserById(int userId);
}
