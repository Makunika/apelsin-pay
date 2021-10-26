package ru.pshiblo.transaction.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Maxim Pshiblo
 */
@FeignClient(value = "auth-service")
public interface AuthServiceClient {

    @RequestMapping(value = "/app/check/token", method = RequestMethod.POST)
    Boolean checkToken(@RequestBody String token);
}
