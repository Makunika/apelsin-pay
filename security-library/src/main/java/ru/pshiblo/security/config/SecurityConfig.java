package ru.pshiblo.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.openfeign.security.OAuth2FeignRequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.DefaultOAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.security.oauth2.server.resource.authentication.DelegatingJwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import ru.pshiblo.security.feign.CustomErrorDecoder;
import ru.pshiblo.security.jwt.CustomJwtGrantedAuthoritiesConverter;

@Configuration
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
                .authorizeRequests(authz -> authz
                        .antMatchers("/actuator/**", "/public/**", "/error/**").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(h -> h.accessDeniedHandler((request, response, accessDeniedException) ->
                        System.out.println("Говно"))
                )
                .oauth2ResourceServer(
                        oauth2 -> oauth2.jwt()
                                .jwtAuthenticationConverter(jwtAuthenticationConverter));
    }

    @Override
    public void configure(WebSecurity webSecurity) throws Exception {
        webSecurity.ignoring().antMatchers("/actuator/**", "/public/**", "/error/**");
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "security.oauth2.client")
    public ClientCredentialsResourceDetails clientCredentialsResourceDetails() {
        return new ClientCredentialsResourceDetails();
    }

    @Bean
    @ConditionalOnMissingBean
    public RequestInterceptor oauth2RequestInterceptor(ClientCredentialsResourceDetails resourceDetails) {
        return new OAuth2FeignRequestInterceptor(new DefaultOAuth2ClientContext(), resourceDetails);
    }

    @Bean
    @ConditionalOnMissingBean
    public OAuth2RestTemplate oAuth2RestTemplate(ClientCredentialsResourceDetails resourceDetails) {
        return new OAuth2RestTemplate(resourceDetails);
    }

    @Bean
    @ConditionalOnMissingBean
    public ErrorDecoder errorDecoder() {
        return new CustomErrorDecoder();
    }

}
