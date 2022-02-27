package ru.pshiblo.info.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.pshiblo.common.protocol.user.UserInfo;
import ru.pshiblo.info.client.AuthClient;
import ru.pshiblo.security.AuthUtils;

@RestController
@RequiredArgsConstructor
public class PingController {

    private final AuthClient authClient;

    @PreAuthorize("hasAuthority('SCOPE_user')")
    @GetMapping("/ping")
    public UserInfo get() {
        return authClient.getUserInfo(AuthUtils.getUserId());
    }
}
