package ru.pshiblo.users.mappings;

import org.mapstruct.Mapper;
import ru.pshiblo.users.domain.User;
import ru.pshiblo.users.web.dto.request.RegisterRequestDto;
import ru.pshiblo.users.web.dto.response.RegisterResponseDto;
import ru.pshiblo.users.web.dto.response.UserDto;
import ru.pshiblo.common.mappings.CommonMapper;

/**
 * @author Maxim Pshiblo
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends CommonMapper<RegisterRequestDto, RegisterResponseDto, User> {
    UserDto toDto(User user);
    User toEntity(UserDto dto);
}
