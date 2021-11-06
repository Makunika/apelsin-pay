package ru.pshiblo.transaction.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.pshiblo.security.jwt.JwtTokenProvider;
import ru.pshiblo.security.jwt.properties.SecurityProperties;
import ru.pshiblo.transaction.feign.AuthServiceClient;
import ru.pshiblo.transaction.security.JwtTokenProviderAdditional;

/**
 * @author Maxim Pshiblo
 */
@Configuration
public class JwtSecurityTransactionConfig {

    @Bean
    @Primary
    public JwtTokenProvider beanJwtTokenProvider(ObjectMapper objectMapper, SecurityProperties securityProperties,
                                                 AuthServiceClient authServiceClient) {
        return new JwtTokenProviderAdditional(objectMapper, securityProperties, authServiceClient);
    }
}
