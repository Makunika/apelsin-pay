package ru.pshiblo.payment.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "info-business-service")
public interface InfoBusinessClient {

    @PostMapping("/company/{companyId}/check/key")
    ResponseEntity<Boolean> checkApiKey(@PathVariable long companyId, @RequestBody String apiKey);
}
