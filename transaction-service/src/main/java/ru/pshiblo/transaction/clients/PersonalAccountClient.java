package ru.pshiblo.transaction.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.pshiblo.transaction.clients.config.InternalClientConfig;
import ru.pshiblo.transaction.clients.model.PersonalAccountResponseDto;

@FeignClient(
        contextId = "internalPersonalId",
        name = "account-personal-service",
        configuration = InternalClientConfig.class
)
public interface PersonalAccountClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/app/personal/check/{userId}/{number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkPersonalAccount(@PathVariable("userId") long userId, @PathVariable("number") String number);

    @GetMapping("/api/app/personal/number/{number}")
    PersonalAccountResponseDto getAccountByNumber(@PathVariable("number") String number);
}
