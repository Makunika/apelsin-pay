package ru.pshiblo.info.client;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ru.pshiblo.common.protocol.user.UserInfo;

@FeignClient(name = "auth-service")
public interface AuthClient {

    @RequestMapping(method = RequestMethod.GET, value = "/auth/api/user/userinfo/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    UserInfo getUserInfo(@PathVariable("userId") Long userId);

    @RequestMapping(method = RequestMethod.GET, value = "/auth/api/user/passport/{userId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    String getPassport(@PathVariable("userId") Long userId);

}
