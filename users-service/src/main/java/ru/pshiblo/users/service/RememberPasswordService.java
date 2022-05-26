package ru.pshiblo.users.service;

public interface RememberPasswordService {
    void rememberPassword(String email);
    void changePasswordByToken(String token, String newPassword);
}
