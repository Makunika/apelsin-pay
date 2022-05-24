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
public class ApelsinAccessTokenConverter  {

    private final JwtAccessTokenConverterConfigurer jwtAccessTokenConverterConfigurer;

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverterConfigurer.configure(jwtAccessTokenConverter);
        return jwtAccessTokenConverter;
    }
}
