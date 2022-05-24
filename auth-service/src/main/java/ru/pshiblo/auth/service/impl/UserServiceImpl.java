package ru.pshiblo.auth.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.pshiblo.auth.clients.PersonalInfoClient;
import ru.pshiblo.auth.clients.UsersClient;
import ru.pshiblo.auth.clients.dto.PersonInfo;
import ru.pshiblo.auth.clients.dto.User;
import ru.pshiblo.auth.model.AuthUser;
import ru.pshiblo.common.exception.IntegrationException;
import ru.pshiblo.common.exception.NotFoundException;

/**
 * @author Maxim Pshiblo
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserDetailsService {

    private final UsersClient usersClient;
    private final PersonalInfoClient personalInfoClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = usersClient.getByUsername(username);
            PersonInfo personInfo = personalInfoClient.getByUserId(user.getId());
            return AuthUser.fromUser(
                    user,
                    personInfo.getEmail(),
                    personInfo.getLastName() + " " + personInfo.getFirstName(),
                    personInfo.getStatus(),
                    personInfo.getLock());
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (IntegrationException e) {
            if (e.getStatus() == 404) {
                throw new UsernameNotFoundException(e.getMessage());
            }
            throw e;
        }
    }
}
