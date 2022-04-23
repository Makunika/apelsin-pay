package ru.pshiblo.auth.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.pshiblo.auth.clients.dto.CompanyUserDto;

import java.util.List;

@FeignClient(name = "info-business-service")
public interface BusinessInfoClient {
    @GetMapping(value = "/company/user/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    List<CompanyUserDto> findByUser(@PathVariable long id);
}
