package ru.pshiblo.security.model;

import org.springframework.security.core.GrantedAuthority;
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
    private final List<String> companies;
    private final List<String> authoritiesSimple;
    private final Collection<? extends GrantedAuthority> authorities;

    public AuthUser(String login,
                    long id,
                    String name,
                    String email,
                    boolean isServer,
                    ConfirmedStatus status,
                    List<String> companies,
                    Collection<? extends GrantedAuthority> authorities) {
        this.login = login;
        this.id = id;
        this.name = name;
        this.email = email;
        this.isServer = isServer;
        this.status = status;
        this.companies = companies;
        this.authoritiesSimple = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
        this.authorities = authorities;
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

    public List<String> getCompanies() {
        return companies;
    }

    public boolean isAdmin() {
        return getAuthoritiesSimple().contains("ROLE_ADMINISTRATOR");
    }
}
