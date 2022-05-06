package ru.pshiblo.account.business.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.pshiblo.account.business.clients.model.CompanyUser;

import java.util.List;

@FeignClient(name = "info-business-service")
public interface InfoBusinessClient {
    @GetMapping("/company/user/{id}")
    List<CompanyUser> findByUser(@PathVariable("id") long id);
}
