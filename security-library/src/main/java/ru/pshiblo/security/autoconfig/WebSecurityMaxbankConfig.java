package ru.pshiblo.security.autoconfig;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import ru.pshiblo.security.jwt.JwtConfigurer;
import ru.pshiblo.security.jwt.JwtTokenProvider;
import ru.pshiblo.security.jwt.properties.SecurityProperties;

/**
 * @author Maxim Pshiblo
 */
@EnableConfigurationProperties(SecurityProperties.class)
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 10)
public class WebSecurityMaxbankConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityProperties securityProperties;

    public WebSecurityMaxbankConfig(JwtTokenProvider jwtTokenProvider, SecurityProperties securityProperties) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.securityProperties = securityProperties;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(securityProperties.getWhiteListUrl().toArray(String[]::new)).permitAll()
                .antMatchers("/app/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new JwtConfigurer(jwtTokenProvider));
    }

}
