package ru.pshiblo.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import liquibase.pro.packaged.D;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.domain.Role;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.model.AuthTokens;
import ru.pshiblo.auth.properties.JwtProperties;
import ru.pshiblo.auth.service.interfaces.JwtService;
import ru.pshiblo.exception.InternalException;
import ru.pshiblo.protocol.user.JwtUser;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Maxim Pshiblo
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {

    private final ObjectMapper objectMapper;
    private final JwtProperties jwtProperties;

    private String secret;

    @PostConstruct
    public void init() {

    }

    @Override
    public AuthTokens generateTokens(User user) {
        try {
            JwtUser jwtUser = new JwtUser(
                    user.getId(),
                    user.getFirstName(),
                    user.getEmail(),
                    user.getRoles().stream().map(Role::getName).collect(Collectors.toList())
            );

            String jsonUser = objectMapper.writeValueAsString(jwtUser);
            Map<String, Object> claims = new HashMap<>();
            claims.put("context", jsonUser);

            String refresh = Jwts.builder()
                    .setExpiration(Date.from(
                            LocalDate.now().plusDays(1)
                                    .atStartOfDay()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()))
                    .setIssuedAt(new Date())
                    .setSubject(String.valueOf(user.getId()))
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();

            String jwt = Jwts.builder()
                    .setExpiration(Date.from(
                            LocalDate.now().plus(Duration.ofHours(jwtProperties.getExpiredHours()))
                                    .atStartOfDay()
                                    .atZone(ZoneId.systemDefault())
                                    .toInstant()))
                    .setIssuedAt(new Date())
                    .setSubject(String.valueOf(user.getId()))
                    .setClaims(claims)
                    .signWith(SignatureAlgorithm.HS512, secret)
                    .compact();

            return new AuthTokens(jwt, refresh);
        }catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new InternalException(e.getMessage(), e);
        }
    }

    @Override
    public boolean validateToken(String token) {
        Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
        return claims.getBody().getExpiration().before(new Date());
    }

    @Override
    public JwtUser getJwtUser(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return objectMapper.readValue(claims.getBody().get("context", String.class), JwtUser.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new SecurityException(e.getMessage(), e);
        }
    }
}
