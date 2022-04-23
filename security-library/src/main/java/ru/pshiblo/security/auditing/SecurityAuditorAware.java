package ru.pshiblo.security.auditing;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNullApi;
import ru.pshiblo.security.AuthUtils;
import ru.pshiblo.security.model.AuthUser;

import java.util.Optional;

public class SecurityAuditorAware implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        AuthUser authUser = AuthUtils.getAuthUser();
        return Optional.of(authUser.getLogin());
    }
}
