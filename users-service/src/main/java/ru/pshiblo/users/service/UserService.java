package ru.pshiblo.users.service;

import ru.pshiblo.users.domain.User;

/**
 * @author Maxim Pshiblo
 */
public interface UserService {
    User getUserById(int userId);
    User getUserByEmail(String email);
    User getByUsername(String username);
}
