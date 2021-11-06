package ru.pshiblo.transaction.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import ru.pshiblo.common.protocol.user.JwtUser;
import ru.pshiblo.security.jwt.JwtTokenProvider;
import ru.pshiblo.security.jwt.exception.JwtAuthenticationException;
import ru.pshiblo.security.jwt.properties.SecurityProperties;
import ru.pshiblo.transaction.feign.AuthServiceClient;

import java.util.Date;
import java.util.List;

/**
 * @author Maxim Pshiblo
 */
public class JwtTokenProviderAdditional extends JwtTokenProvider {

    private final AuthServiceClient authServiceClient;

    public JwtTokenProviderAdditional(ObjectMapper objectMapper, SecurityProperties securityProperties, AuthServiceClient authServiceClient) {
        super(objectMapper, securityProperties);
        this.authServiceClient = authServiceClient;
    }

    @Override
    public JwtUser getJwtUserDto(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if (claims.getBody().getSubject().equals("app")) {
                return new JwtUser(-10, "app", "email", List.of("list"));
            }
            if (claims.getBody().getExpiration().before(new Date())) {
                if (authServiceClient.checkToken(token)) {
                    return objectMapper.readValue(claims.getBody().get("context", String.class), JwtUser.class);
                }
            }
            throw new JwtAuthenticationException("JWT token is invalid");
        } catch (JwtException | IllegalArgumentException | JsonProcessingException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }
}
