package ru.pshiblo.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pshiblo.users.domain.Role;
import ru.pshiblo.users.domain.User;
import ru.pshiblo.users.repository.RoleRepository;
import ru.pshiblo.users.repository.UserRepository;
import ru.pshiblo.users.service.interfaces.RegisterService;
import ru.pshiblo.common.exception.AlreadyExistException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.common.exception.SecurityException;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User registerUser(String login, String password) {
        if (userRepository.existsByLogin(login)) {
            throw new AlreadyExistException("User already exist");
        }
        String passwordHash = passwordEncoder.encode(password);
        User user = new User();
        user.setRoles(List.of(getUserRole()));
        user.setPasswordHash(passwordHash);
        user.setLogin(login);
        user = userRepository.save(user);
        return user;
    }

    @Cacheable(value = "user_role")
    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER");
    }

    @Override
    public void changePassword(String login, String password, String newPassword) {
        User user = userRepository.findByLogin(login).orElseThrow(() -> new NotFoundException(login, User.class));
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new SecurityException();
        }
        String newPasswordHash = passwordEncoder.encode(newPassword);
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
    }
}
