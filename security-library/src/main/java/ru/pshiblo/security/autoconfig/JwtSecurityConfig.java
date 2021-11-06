package ru.pshiblo.security.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.pshiblo.security.jwt.JwtTokenProvider;
import ru.pshiblo.security.jwt.properties.SecurityProperties;

/**
 * @author Maxim Pshiblo
 */
@Configuration
public class JwtSecurityConfig {

    @Bean
    @ConditionalOnMissingBean
    public JwtTokenProvider beanJwtTokenProvider(ObjectMapper objectMapper, SecurityProperties securityProperties) {
        return new JwtTokenProvider(objectMapper, securityProperties);
    }
}
