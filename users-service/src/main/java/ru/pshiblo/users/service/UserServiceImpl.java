package ru.pshiblo.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.users.domain.User;
import ru.pshiblo.users.repository.UserRepository;
import ru.pshiblo.users.service.interfaces.UserService;
import ru.pshiblo.common.exception.NotFoundException;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId, User.class));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new NotFoundException(username, User.class));
    }
}
