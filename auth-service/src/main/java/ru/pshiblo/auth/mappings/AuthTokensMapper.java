package ru.pshiblo.auth.mappings;

import org.mapstruct.Mapper;
import ru.pshiblo.auth.model.AuthTokens;
import ru.pshiblo.auth.web.dto.response.AuthTokensDto;
import ru.pshiblo.mappings.ResponseMapper;

/**
 * @author Maxim Pshiblo
 */
@Mapper(componentModel = "spring")
public interface AuthTokensMapper extends ResponseMapper<AuthTokensDto, AuthTokens> {
}
