package ru.pshiblo.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Provider;
import java.security.Security;

@SpringBootApplication
public class AuthApplication {

    public static void main(String[] args) {
        Provider[] providers = Security.getProviders();
        SpringApplication.run(AuthApplication.class, args);
    }

}
