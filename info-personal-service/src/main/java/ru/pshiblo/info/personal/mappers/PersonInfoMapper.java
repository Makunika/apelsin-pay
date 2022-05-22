package ru.pshiblo.info.personal.mappers;

import org.mapstruct.Mapper;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;
import ru.pshiblo.info.personal.web.dto.RegisterPersonInfoDto;
import ru.pshiblo.info.personal.web.dto.UpdatePersonInfoDto;

@Mapper(componentModel = "spring")
public interface PersonInfoMapper {
    PersonInfo toEntity(RegisterPersonInfoDto request);
    PersonInfo toEntity(UpdatePersonInfoDto request, long id);
    PersonInfoDto toDTO(PersonInfo personInfo);
}
