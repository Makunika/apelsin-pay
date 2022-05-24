package ru.pshiblo.security.model;

import org.springframework.security.core.GrantedAuthority;
import ru.pshiblo.security.AuthContants;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class AuthUser {
    private final String login;
    private final long id;
    private final String name;
    private final String email;
    private final boolean isServer;
    private final ConfirmedStatus status;
    private final List<String> authoritiesSimple;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Boolean isLock;

    public AuthUser(String login,
                    long id,
                    String name,
                    String email,
                    boolean isServer,
                    ConfirmedStatus status,
                    Collection<? extends GrantedAuthority> authorities,
                    Boolean isLock) {
        this.login = login;
        this.id = id;
        this.name = name;
        this.email = email;
        this.isServer = isServer;
        this.status = status;
        this.authoritiesSimple = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        this.authorities = authorities;
        this.isLock = isLock;
    }

    public String getLogin() {
        return login;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getAuthoritiesSimple() {
        return authoritiesSimple;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public boolean isServer() {
        return isServer;
    }

    public ConfirmedStatus getStatus() {
        return status;
    }

    public boolean isAdmin() {
        return getAuthoritiesSimple().contains(AuthContants.ROLE_ADMIN);
    }

    public Boolean getLock() {
        return isLock;
    }
}
