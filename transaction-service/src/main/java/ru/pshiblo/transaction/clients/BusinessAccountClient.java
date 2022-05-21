package ru.pshiblo.transaction.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.security.config.SecurityConfig;
import ru.pshiblo.transaction.clients.config.InternalClientConfig;
import ru.pshiblo.transaction.clients.model.BusinessAccountResponseDto;

@FeignClient(
        contextId = "internalBusinessId",
        name = "account-business-service",
        configuration = InternalClientConfig.class
)
public interface BusinessAccountClient {
    @RequestMapping(method = RequestMethod.POST, value = "/api/app/business/check/{userId}/{number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkBusinessAccount(@PathVariable("userId") long userId, @PathVariable("number") String number);

    @GetMapping("/api/app/business/number/{number}")
    BusinessAccountResponseDto getAccountByNumber(@PathVariable("number") String number);
}
