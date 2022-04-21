package ru.pshiblo.info.personal.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pshiblo.info.personal.domain.PersonInfo;
import ru.pshiblo.info.personal.web.dto.PersonInfoDto;
import ru.pshiblo.info.personal.web.dto.RegisterPersonInfoDto;

@Mapper(componentModel = "spring")
public interface PersonInfoMapper {
    PersonInfo registerPersonInfoDtoToPersonInfo(RegisterPersonInfoDto registerPersonInfoDto);

    RegisterPersonInfoDto personInfoToRegisterPersonInfoDto(PersonInfo personInfo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonInfoFromRegisterPersonInfoDto(RegisterPersonInfoDto registerPersonInfoDto, @MappingTarget PersonInfo personInfo);

    PersonInfo personInfoDtoToPersonInfo(PersonInfoDto personInfoDto);

    PersonInfoDto personInfoToPersonInfoDto(PersonInfo personInfo);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonInfoFromPersonInfoDto(PersonInfoDto personInfoDto, @MappingTarget PersonInfo personInfo);
}
