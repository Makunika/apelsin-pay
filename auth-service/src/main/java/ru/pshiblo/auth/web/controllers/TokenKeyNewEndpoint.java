package ru.pshiblo.auth.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TokenKeyNewEndpoint {

    private final JwtAccessTokenConverter converter;

    @GetMapping("oauth/token_key_new")
    public String getPublicKey(Principal principal) {
        if ((principal == null || principal instanceof AnonymousAuthenticationToken) && !converter.isPublic()) {
            throw new AccessDeniedException("You need to authenticate to see a shared key");
        }
        log.info("Get key!");
        Map<String, String> result = converter.getKey();
        return result.getOrDefault("value", "").replaceAll("\n", "");
    }

}
