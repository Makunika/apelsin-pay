package ru.pshiblo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.domain.Token;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.domain.UserPassword;
import ru.pshiblo.auth.encoder.Hmac512PasswordEncoder;
import ru.pshiblo.auth.model.AuthTokens;
import ru.pshiblo.auth.repository.TokenRepository;
import ru.pshiblo.auth.repository.UserPasswordRepository;
import ru.pshiblo.auth.repository.UserRepository;
import ru.pshiblo.auth.service.interfaces.AuthService;
import ru.pshiblo.auth.service.interfaces.JwtService;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.common.exception.SecurityException;
import ru.pshiblo.common.protocol.user.JwtUser;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserPasswordRepository userPasswordRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final Hmac512PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthTokens loginWithPassword(String login, String password) {
        UserPassword userPassword = userPasswordRepository.findByLogin(login).orElseThrow(() -> new NotFoundException(login, User.class));

        if (!passwordEncoder.matches(password, userPassword.getPasswordHash())) {
            throw new SecurityException();
        }

        AuthTokens authTokens = jwtService.generateTokens(userPassword.getUser());

        Token token = new Token();
        token.setToken(authTokens.getRefresh());
        token.setUser(userPassword.getUser());
        tokenRepository.save(token);

        Token tokenJwt = new Token();
        tokenJwt.setToken(authTokens.getJwt());
        tokenJwt.setUser(userPassword.getUser());
        tokenRepository.save(tokenJwt);

        return authTokens;
    }

    @Override
    public AuthTokens loginWithRefreshToken(String token) {
        Token tokenFromDb = tokenRepository.findByToken(token).orElseThrow(SecurityException::new);

        if (!jwtService.validateToken(token)) {
            throw new SecurityException();
        }
        tokenRepository.delete(tokenFromDb);

        JwtUser jwtUser = jwtService.getJwtUser(token);
        User user = userRepository.findById(jwtUser.getId()).orElseThrow(() -> new SecurityException("User not found"));
        AuthTokens authTokens = jwtService.generateTokens(user);

        Token newToken = new Token();
        newToken.setToken(authTokens.getRefresh());
        newToken.setUser(user);
        tokenRepository.save(newToken);

        Token tokenJwt = new Token();
        tokenJwt.setToken(authTokens.getJwt());
        tokenJwt.setUser(user);
        tokenRepository.save(tokenJwt);

        return authTokens;
    }

    @Override
    public boolean checkJwtTokenInDb(String token) {
        return tokenRepository.findByToken(token).isPresent();
    }

}
