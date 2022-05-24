package ru.pshiblo.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import ru.pshiblo.auth.model.AuthUser;
import ru.pshiblo.auth.service.KeyGeneratorService;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class ApelsinAccessTokenConverterConfig implements JwtAccessTokenConverterConfigurer {

    private final KeyGeneratorService keyGeneratorService;
    @Bean
    public UserAuthenticationConverter userAuthenticationConverter(){
        return new DefaultUserAuthenticationConverter() {
            @Override
            public Map<String, ?> convertUserAuthentication(Authentication authentication) {
                Map<String, Object> userMap = (Map<String, Object>) super.convertUserAuthentication(authentication);
                if (authentication.getPrincipal() instanceof AuthUser) {
                    AuthUser authUser = (AuthUser) authentication.getPrincipal();
                    userMap.put("id", authUser.getId());
                    userMap.put("email", authUser.getEmail());
                    userMap.put("name", authUser.getName());
                    userMap.put("status", authUser.getConfirmedStatus());
                    userMap.put("lock", authUser.getIsLock());
                }
                return userMap;
            }
        };
    }

    @Override
    public void configure(JwtAccessTokenConverter jwtAccessTokenConverter) {
        try {
            DefaultAccessTokenConverter tokenConverter = new DefaultAccessTokenConverter();
            tokenConverter.setUserTokenConverter(userAuthenticationConverter());
            jwtAccessTokenConverter.setAccessTokenConverter(tokenConverter);
            jwtAccessTokenConverter.setKeyPair(keyGeneratorService.getKey());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
