package ru.pshiblo.auth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.pshiblo.auth.clients.dto.User;

@FeignClient(name = "users-service")
public interface UsersClient {
    @GetMapping("/user/search/username/{username}")
    User getByUsername(@PathVariable("username") String username);
}
