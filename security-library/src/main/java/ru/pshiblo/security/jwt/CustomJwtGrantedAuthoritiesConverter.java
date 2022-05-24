package ru.pshiblo.security.jwt;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;

public class CustomJwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        Object authoritiesClaim = jwt.getClaims().get("authorities");
        if (authoritiesClaim instanceof Collection) {
            Collection<String> authorities = (Collection<String>) authoritiesClaim;
            for (String authority : authorities) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority));
            }
        }
        Object isLock = jwt.getClaims().get("lock");
        if (isLock instanceof Boolean) {
            Boolean lock = (Boolean) isLock;
            if (!lock) {
                grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_UNBAN"));
            }
        } else if (isServer(jwt)) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_UNBAN"));
        }
        return grantedAuthorities;
    }

    private boolean isServer(Jwt jwt) {
        Object scopes = jwt.getClaims().get("scope");
        if (scopes instanceof Collection) {
            Collection<String> scopesAuth = (Collection<String>) scopes;
            for (String scope : scopesAuth) {
                if ("server".equals(scope)) {
                    return true;
                }
            }
        }
        return false;
    }
}
