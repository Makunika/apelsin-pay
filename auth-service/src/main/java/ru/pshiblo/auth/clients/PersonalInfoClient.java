package ru.pshiblo.auth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.pshiblo.auth.clients.dto.PersonInfo;

@FeignClient(name = "info-personal-service")
public interface PersonalInfoClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/persons/user/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    PersonInfo getByUserId(@PathVariable("id") long id);
}
