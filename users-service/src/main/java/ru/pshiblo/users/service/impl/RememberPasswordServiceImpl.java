package ru.pshiblo.users.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.pshiblo.common.exception.ApelsinException;
import ru.pshiblo.common.exception.NotFoundException;
import ru.pshiblo.users.domain.RememberPassword;
import ru.pshiblo.users.domain.User;
import ru.pshiblo.users.repository.RememberPasswordRepository;
import ru.pshiblo.users.repository.UserRepository;
import ru.pshiblo.users.service.RememberPasswordService;
import ru.pshiblo.users.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RememberPasswordServiceImpl implements RememberPasswordService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final UserService userService;
    private final RememberPasswordRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void rememberPassword(String email) {
        User user = userService.getUserByEmail(email);
        String token = RandomStringUtils.randomAlphabetic(20);
        while (repository.existsByToken(token)) {
            token = RandomStringUtils.randomAlphabetic(20);
        }
        RememberPassword rememberPassword = new RememberPassword();
        rememberPassword.setToken(token);
        rememberPassword.setUser(user);
        rememberPassword.setUntilSeconds(3600);
        repository.save(rememberPassword);

        sendEmailRememberPassword(token, user.getEmail(), user.getLogin());
    }

    @Override
    public void changePasswordByToken(String token, String newPassword) {
        RememberPassword rememberPassword = repository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(token, "Токен"));

        if (rememberPassword.getCreated().plusSeconds(rememberPassword.getUntilSeconds()).isBefore(LocalDateTime.now())) {
            throw new ApelsinException("У токена истек срок действия");
        }

        if (!rememberPassword.isValid()) {
            throw new ApelsinException("Токен уже был использован");
        }

        rememberPassword.setValid(false);
        repository.save(rememberPassword);
        User user = rememberPassword.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void sendEmailRememberPassword(String token, String email, String login) {
        String url = "http://pshiblo.xyz/reset-password/check?" + token;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("apelsin.pay@yandex.ru");
        message.setTo(email);
        message.setSubject("Восстановление пароля | Apelsin");
        message.setText("Добрый день," + login + "!\nДля восстановления пароля перейдите по ссылке: " + url);
        mailSender.send(message);
    }

}
