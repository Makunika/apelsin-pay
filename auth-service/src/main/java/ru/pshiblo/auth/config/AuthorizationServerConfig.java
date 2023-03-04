package ru.pshiblo.auth.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

@Configuration
@EnableAuthorizationServer
@RequiredArgsConstructor
@Order(1)
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtAccessTokenConverter jwtAccessTokenConverter;
    private final Environment env;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("permitAll()")
                .passwordEncoder(NoOpPasswordEncoder.getInstance());

    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //clients.jdbc(dataSource)
        clients.inMemory()
                .withClient("browser_main")
                .authorizedGrantTypes("refresh_token", "authorization_code")
                .redirectUris("https://oidcdebugger.com/debug", "https://oauth.pstmn.io/v1/browser-callback", "http://localhost:3000/login", "http://graduate.pshiblo.xyz/login", env.getProperty("URL_LOGIN_MAIN"))
                .autoApprove(true)
                .scopes("user")
                .and()

                .withClient("browser_payment")
                .authorizedGrantTypes("implicit")
                .redirectUris("https://oidcdebugger.com/debug", "https://oauth.pstmn.io/v1/browser-callback", "http://localhost:3006/login", "http://payment.graduate.pshiblo.xyz/login", env.getProperty("URL_LOGIN_PAYMENT"))
                .autoApprove(true)
                .scopes("user_payment")
                .and()

                .withClient("info-personal-service")
                .secret(env.getProperty("INFO_PERSONAL_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server", "users_s")
                .and()

                .withClient("info-business-service")
                .secret(env.getProperty("INFO_BUSINESS_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server")
                .and()

                .withClient("users-service")
                .secret(env.getProperty("USERS_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server")
                .and()

                .withClient("auth-service")
                .secret(env.getProperty("AUTH_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server", "info_b_s", "info_p_s")
                .and()

                .withClient("transaction-service")
                .secret(env.getProperty("TRANSACTION_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server", "account_b_s", "account_p_s")
                .and()

                .withClient("account-business-service")
                .secret(env.getProperty("ACCOUNT_BUSINESS_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server", "info_b_s")
                .and()

                .withClient("payment-service")
                .secret(env.getProperty("PAYMENT_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server", "account_b_s", "info_b_s", "transaction_s")
                .and()

                .withClient("account-personal-service")
                .secret(env.getProperty("ACCOUNT_PERSONAL_SERVICE_PASSWORD"))
                .authorizedGrantTypes("client_credentials", "refresh_token")
                .scopes("server", "transaction_s");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints
                .authenticationManager(authenticationManager)
                .userDetailsService(userDetailsService)
                .tokenStore(new InMemoryTokenStore())
                .accessTokenConverter(jwtAccessTokenConverter);
    }
}
