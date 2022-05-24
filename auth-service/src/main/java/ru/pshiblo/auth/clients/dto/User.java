package ru.pshiblo.auth.clients.dto;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private Integer id;
    private String passwordHash;
    private String login;
    private List<Role> roles;
}