package ru.pshiblo.auth.service.interfaces;

import ru.pshiblo.auth.domain.User;

/**
 * @author Maxim Pshiblo
 */
public interface RegisterService {
    User registerUser(User user, String login, String password);
    void changePassword(String login, String password, String newPassword);
}
