package ru.pshiblo.info;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class InfoServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InfoServiceApplication.class, args);
    }

}
