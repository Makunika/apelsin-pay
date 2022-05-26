package ru.pshiblo.users.service;

import ru.pshiblo.users.domain.User;

/**
 * @author Maxim Pshiblo
 */
public interface RegisterService {
    User registerUser(String login, String password, String email);
    void changePassword(long userId, String password, String newPassword);
}
