package ru.pshiblo.users.service;

import ru.pshiblo.users.domain.User;

/**
 * @author Maxim Pshiblo
 */
public interface RegisterService {
    User registerUser(String login, String password, String email);
    void changePassword(long userId, String password, String newPassword);

    /** Сохраняет уже закодированный хеш (миграция legacy-хешей в bcrypt из auth-service). */
    void updatePasswordHash(int userId, String encodedPasswordHash);
}
