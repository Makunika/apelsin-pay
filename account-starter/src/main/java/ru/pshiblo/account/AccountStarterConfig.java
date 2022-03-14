package ru.pshiblo.account;

import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ComponentScan
@EnableJpaRepositories
@EnableJpaAuditing
@EntityScan
public class AccountStarterConfig {
}
