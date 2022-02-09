package ru.pshiblo.auth.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Data
@Table(name = "passwords")
@Entity
public class UserPassword {

    @JsonIgnore
    @Id
    @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @JsonIgnore
    @Column(name = "password_hash", nullable = false, length = 200)
    private String passwordHash;

    @Column(name = "login", nullable = false, length = 100)
    private String login;

    @JsonIgnore
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