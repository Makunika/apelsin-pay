package ru.pshiblo.auth.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.pshiblo.auth.domain.Role;
import ru.pshiblo.auth.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonAutoDetect
public class AuthUser implements UserDetails {

    private String login;
    private String email;
    private String name;
    private int id;
    private List<String> roles;

    @JsonIgnore
    private String passwordHash;

    public static AuthUser fromUser(User user) {
        return new AuthUser(
                user.getUserPassword().getLogin(),
                user.getEmail(),
                user.getName(),
                user.getId(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toList()),
                user.getUserPassword().getPasswordHash()
        );
    }


    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return passwordHash;
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return login;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}
