package ru.pshiblo.info.personal.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.pshiblo.info.personal.clients.model.RegisterAuthDto;
import ru.pshiblo.info.personal.clients.model.UserIdDto;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.POST, value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    UserIdDto register(@RequestBody RegisterAuthDto request);
}
