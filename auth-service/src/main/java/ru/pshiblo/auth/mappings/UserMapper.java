package ru.pshiblo.auth.mappings;

import org.mapstruct.Mapper;
import ru.pshiblo.auth.domain.User;
import ru.pshiblo.auth.web.dto.request.RegisterRequestDto;
import ru.pshiblo.auth.web.dto.response.RegisterResponseDto;
import ru.pshiblo.common.mappings.CommonMapper;

/**
 * @author Maxim Pshiblo
 */
@Mapper(componentModel = "spring")
public interface UserMapper extends CommonMapper<RegisterRequestDto, RegisterResponseDto, User> {
}
