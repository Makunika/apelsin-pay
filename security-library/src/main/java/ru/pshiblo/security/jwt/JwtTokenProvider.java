package ru.pshiblo.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.pshiblo.security.jwt.exception.JwtAuthenticationException;
import ru.pshiblo.security.jwt.properties.SecurityProperties;
import ru.pshiblo.common.protocol.user.JwtUser;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@RequiredArgsConstructor
public class JwtTokenProvider {

    protected final ObjectMapper objectMapper;
    protected final SecurityProperties securityProperties;

    protected String secret;

    @PostConstruct
    protected void init() {
        secret = Base64.getEncoder().encodeToString(securityProperties.getJwtSecret().getBytes());
    }

    public Authentication getAuthentication(String token) {
        JwtUser jwtUserDto = getJwtUserDto(token);
        if (jwtUserDto != null) {
            AuthUser authUser = new AuthUser(jwtUserDto.getId(),
                    jwtUserDto.getUsername(),
                    jwtUserDto.getEmail(),
                    jwtUserDto.getRoles(),
                    jwtUserDto.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
            return new UsernamePasswordAuthenticationToken(authUser, "", authUser.getAuthorities());
        } else {
            return null;
        }
    }

    public String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Auth_")) {
            return bearerToken.substring(5);
        }
        return null;
    }

    public JwtUser getJwtUserDto(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            if (claims.getBody().getSubject().equals("app")) {
                return new JwtUser(-10, "app", "email", List.of("list"));
            }
            if (claims.getBody().getExpiration().before(new Date())) {
                return objectMapper.readValue(claims.getBody().get("context", String.class), JwtUser.class);
            }
            return null;
        } catch (JwtException | IllegalArgumentException | JsonProcessingException e) {
            throw new JwtAuthenticationException("JWT token is expired or invalid");
        }
    }
}
