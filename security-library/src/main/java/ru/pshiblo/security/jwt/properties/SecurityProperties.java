package ru.pshiblo.security.jwt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Maxim Pshiblo
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
    private String jwtSecret;
    private String jwtAppSecret;
    private List<String> whiteListUrl;
}
