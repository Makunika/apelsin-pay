package ru.pshiblo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.domain.Role;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.domain.UserPassword;
import ru.pshiblo.auth.encoder.Hmac512PasswordEncoder;
import ru.pshiblo.auth.repository.RoleRepository;
import ru.pshiblo.auth.repository.UserPasswordRepository;
import ru.pshiblo.auth.repository.UserRepository;
import ru.pshiblo.auth.service.interfaces.RegisterService;
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

    private final UserPasswordRepository userPasswordRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public User registerUser(User user, String login, String password) {
        if (userRepository.existsByPassportNumberAndPassportSeries(user.getPassportNumber(), user.getPassportSeries())
                || userRepository.existsByEmail(user.getEmail())
                || userPasswordRepository.existsByLogin(login)) {
            throw new AlreadyExistException("User already exist");
        }
        String passwordHash = passwordEncoder.encode(password);
        user.setRoles(List.of(getUserRole()));
        user = userRepository.save(user);
        UserPassword userPassword = new UserPassword();
        userPassword.setUser(user);
        userPassword.setPasswordHash(passwordHash);
        userPassword.setLogin(login);
        userPasswordRepository.save(userPassword);
        return user;
    }

    @Cacheable(value = "user_role")
    public Role getUserRole() {
        return roleRepository.findByName("ROLE_USER");
    }

    @Override
    public void changePassword(String login, String password, String newPassword) {
        UserPassword userPassword = userPasswordRepository.findByLogin(login).orElseThrow(() -> new NotFoundException(login, UserPassword.class));
        if (!passwordEncoder.matches(password, userPassword.getPasswordHash())) {
            throw new SecurityException();
        }
        String newPasswordHash = passwordEncoder.encode(newPassword);
        userPassword.setPasswordHash(newPasswordHash);
        userPasswordRepository.save(userPassword);
    }
}
