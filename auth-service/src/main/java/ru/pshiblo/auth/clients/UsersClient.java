package ru.pshiblo.auth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.pshiblo.auth.clients.dto.UpdatePasswordHash;
import ru.pshiblo.auth.clients.dto.User;

@FeignClient(name = "users-service")
public interface UsersClient {

    @GetMapping("/user/search/username/{username}")
    User getByUsername(@PathVariable("username") String username);

    @PutMapping("/internal/users/{id}/password-hash")
    void updatePasswordHash(@PathVariable("id") int id, @RequestBody UpdatePasswordHash request);
}
