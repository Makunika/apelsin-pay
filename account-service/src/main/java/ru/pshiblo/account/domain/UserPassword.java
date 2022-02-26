package ru.pshiblo.account.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "passwords")
@Entity
public class UserPassword {

    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "login", nullable = false, length = 100)
    private String login;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Override
    public String toString() {
        return "UserPassword{" +
                "id='" + id + '\'' +
                ", passwordHash='" + passwordHash + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}