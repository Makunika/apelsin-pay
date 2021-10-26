package ru.pshiblo.auth.service.interfaces;

import ru.pshiblo.auth.model.AuthTokens;

/**
 * @author Maxim Pshiblo
 */
public interface AuthService {
    AuthTokens loginWithPassword(String login, String password);
    AuthTokens loginWithRefreshToken(String token);
    boolean checkJwtTokenInDb(String token);
}
