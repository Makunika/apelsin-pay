package ru.pshiblo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.domain.Role;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.domain.UserPassword;
import ru.pshiblo.auth.repository.UserPasswordRepository;
import ru.pshiblo.auth.repository.UserRepository;
import ru.pshiblo.auth.service.interfaces.UserService;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.common.protocol.user.LargeUserInfo;
import ru.pshiblo.common.protocol.user.SmallUserInfo;
import ru.pshiblo.common.protocol.user.UserInfo;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;

    @Override
    public LargeUserInfo getLargeUserInfo(int userId) {
        User user = getUserById(userId);
        LargeUserInfo userInfo = new LargeUserInfo();
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setMiddleName(user.getMiddleName());
        userInfo.setId(user.getId());
        UserPassword userPassword = userPasswordRepository.findByUser(user).orElseThrow(() -> new NotFoundException(userId, UserPassword.class));
        userInfo.setLogin(userPassword.getLogin());
        userInfo.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return userInfo;
    }

    @Override
    public UserInfo getUserInfo(int userId) {
        User user = getUserById(userId);
        UserInfo userInfo = new UserInfo();
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setMiddleName(user.getMiddleName());
        userInfo.setId(user.getId());
        return userInfo;
    }

    @Override
    public SmallUserInfo getSmallUserInfo(int userId) {
        User user = getUserById(userId);
        SmallUserInfo userInfo = new SmallUserInfo();
        userInfo.setFirstName(user.getFirstName());
        userInfo.setId(user.getId());
        return userInfo;
    }

    @Override
    public String getPassport(int userId) {
        User user = getUserById(userId);
        return user.getPassportSeries() + " " + user.getPassportNumber();
    }

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
