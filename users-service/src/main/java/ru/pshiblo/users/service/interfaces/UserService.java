package ru.pshiblo.users.service.interfaces;

import ru.pshiblo.users.domain.User;

/**
 * @author Maxim Pshiblo
 */
public interface UserService {
    User getUserById(int userId);
    User getByUsername(String username);
}
