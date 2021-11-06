package ru.pshiblo.auth.service.interfaces;

import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.model.AuthTokens;
import ru.pshiblo.common.protocol.user.JwtUser;

/**
 * @author Maxim Pshiblo
 */
public interface JwtService {
    AuthTokens generateTokens(User user);
    boolean validateToken(String token);
    JwtUser getJwtUser(String token);
}
