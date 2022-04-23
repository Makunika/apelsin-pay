package ru.pshiblo.info.personal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;


@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class InfoPersonalServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfoPersonalServiceApplication.class, args);
    }

}
