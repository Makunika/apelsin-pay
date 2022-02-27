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
        return grantedAuthorities;
    }
}
