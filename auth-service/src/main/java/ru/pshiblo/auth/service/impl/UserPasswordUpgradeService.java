package ru.pshiblo.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.clients.UsersClient;
import ru.pshiblo.auth.clients.dto.UpdatePasswordHash;
import ru.pshiblo.auth.model.AuthUser;

/**
 * Прозрачная миграция паролей: {@code DaoAuthenticationProvider} после успешного входа
 * вызывает {@link #updatePassword}, если {@code PasswordEncoder.upgradeEncoding(hash)} вернул true
 * (хеш без префикса, т.е. legacy HmacSHA512). Новый хеш уже закодирован bcrypt ({@code {bcrypt}...}),
 * сохраняем его через внутренний эндпоинт users-service.
 * Ошибка сохранения не должна ломать вход — пользователь просто останется на старом хеше до следующего раза.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserPasswordUpgradeService implements UserDetailsPasswordService {

    private final UsersClient usersClient;

    @Override
    public UserDetails updatePassword(UserDetails user, String newPassword) {
        if (!(user instanceof AuthUser)) {
            return user;
        }
        AuthUser authUser = (AuthUser) user;
        try {
            usersClient.updatePasswordHash(authUser.getId(), new UpdatePasswordHash(newPassword));
            authUser.setPasswordHash(newPassword);
            log.info("Password hash of user {} upgraded to {}", authUser.getLogin(), newPassword.substring(0, newPassword.indexOf('}') + 1));
        } catch (RuntimeException e) {
            log.error("Failed to upgrade password hash of user {}", authUser.getLogin(), e);
        }
        return authUser;
    }
}
