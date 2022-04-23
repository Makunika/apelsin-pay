package ru.pshiblo.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.pshiblo.auth.clients.PersonalInfoClient;
import ru.pshiblo.auth.clients.dto.PersonInfoDto;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.model.AuthUser;
import ru.pshiblo.auth.repository.UserRepository;
import ru.pshiblo.auth.service.interfaces.UserService;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {

    private final UserService userService;
    private final PersonalInfoClient personalInfoClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userService.getByUsername(username);

        PersonInfoDto personInfoDto = personalInfoClient.getByUserId(user.getId());

        return AuthUser.fromUser(
                user,
                personInfoDto.getEmail(),
                personInfoDto.getLastName() + " " + personInfoDto.getFirstName(),
                personInfoDto.getStatus(),
                //TODO:integration
                new ArrayList<>());
    }
}
