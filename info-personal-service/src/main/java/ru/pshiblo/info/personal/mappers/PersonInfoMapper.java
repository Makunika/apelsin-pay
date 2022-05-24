package ru.pshiblo.info.personal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;
import ru.pshiblo.info.personal.web.dto.RegisterPersonInfoDto;
import ru.pshiblo.info.personal.web.dto.SimplePersonInfoDto;
import ru.pshiblo.info.personal.web.dto.UpdatePersonInfoDto;

@Mapper(componentModel = "spring")
public interface PersonInfoMapper {
    PersonInfo toEntity(RegisterPersonInfoDto request);
    PersonInfo toEntity(UpdatePersonInfoDto request, long id);
    @Mapping(target = "lock", expression = "java(personInfo.getIsLock())")
    PersonInfoDto toDTO(PersonInfo personInfo);

    @Mapping(target = "personInfoId", source = "personInfo.id")
    @Mapping(target = "name", expression = "java(personInfo.getFIO())")
    SimplePersonInfoDto toSimpleDTO(PersonInfo personInfo);
}
