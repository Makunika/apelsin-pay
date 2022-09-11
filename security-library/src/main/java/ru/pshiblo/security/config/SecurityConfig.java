package ru.pshiblo.security.config;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import ru.pshiblo.security.auditing.SecurityAuditorAware;
import ru.pshiblo.security.jwt.CustomJwtGrantedAuthoritiesConverter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new DelegatingJwtGrantedAuthoritiesConverter(
                new JwtGrantedAuthoritiesConverter(),
                new CustomJwtGrantedAuthoritiesConverter()
        ));


        http
                .cors()
                .and()
                .csrf().disable()
                .authorizeRequests(authz -> authz
                        .antMatchers("/actuator", "/actuator/**", "/public/**", "/error/**").permitAll()
                        .anyRequest().access("hasAuthority('ROLE_UNBAN')")
                )
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt()
                                .jwtAuthenticationConverter(jwtAuthenticationConverter));
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers("/actuator", "/actuator/**", "/public/**", "/error/**");
    }

    @Bean
    @ConditionalOnMissingBean
    public SecurityAuditorAware securityAuditorAware() {
        return new SecurityAuditorAware();
    }

}
