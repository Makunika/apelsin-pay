package ru.pshiblo.transaction.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.pshiblo.security.config.SecurityConfig;
import ru.pshiblo.transaction.clients.config.InternalClientConfig;

@FeignClient(
        contextId = "internalBusinessId",
        name = "account-business-service",
        configuration = InternalClientConfig.class
)
public interface BusinessAccountClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/app/business/check/{userId}/{number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkBusinessAccount(@PathVariable("userId") long userId, @PathVariable("number") String number);
}
