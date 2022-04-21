package ru.pshiblo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.auth.domain.Role;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.domain.UserPassword;
import ru.pshiblo.auth.repository.UserPasswordRepository;
import ru.pshiblo.auth.repository.UserRepository;
import ru.pshiblo.auth.service.interfaces.UserService;
import ru.pshiblo.common.exception.NotFoundException;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;

    @Override
    public User getUserById(int userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId, User.class));
    }

    @Override
    public User getByUsername(String username) {
        UserPassword userPassword = userPasswordRepository.findByLogin(username)
                .orElseThrow(() -> new NotFoundException(username, User.class));
        return userPassword.getUser();
    }
}
