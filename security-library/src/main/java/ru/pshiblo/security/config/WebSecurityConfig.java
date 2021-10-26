package ru.pshiblo.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import ru.pshiblo.security.jwt.JwtConfigurer;
import ru.pshiblo.security.jwt.JwtTokenProvider;
import ru.pshiblo.security.jwt.properties.SecurityProperties;

/**
 * @author Maxim Pshiblo
 */
@Configuration
@EnableConfigurationProperties({SecurityProperties.class})
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(securityProperties.getWhiteListUrl().toArray(String[]::new)).authenticated()
                .anyRequest().permitAll()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

}
