package ru.pshiblo.transaction.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = "account-personal-service")
public interface PersonalAccountClient {

    @RequestMapping(method = RequestMethod.POST, value = "/api/app/personal/check/{userId}/{number}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Boolean> checkPersonalAccount(@PathVariable long userId, @PathVariable String number);
}
