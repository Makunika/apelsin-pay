package ru.pshiblo.users.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.users.domain.User;
import ru.pshiblo.users.service.interfaces.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("{id}")
    public User getById(@PathVariable int id) {
        return userService.getUserById(id);
    }

    @GetMapping("/search/username/{username}")
    public User getById(@PathVariable String username) {
        return userService.getByUsername(username);
    }
}
