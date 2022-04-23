package ru.pshiblo.users.service.interfaces;

import ru.pshiblo.users.domain.User;

/**
 * @author Maxim Pshiblo
 */
public interface RegisterService {
    User registerUser(String login, String password);
    void changePassword(String login, String password, String newPassword);
}
