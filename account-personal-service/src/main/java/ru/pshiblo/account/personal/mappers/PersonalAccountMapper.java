package ru.pshiblo.account.personal.mappers;

import org.mapstruct.*;
import ru.pshiblo.account.personal.core.domain.PersonalAccount;
import ru.pshiblo.account.personal.core.domain.PersonalAccountType;
import ru.pshiblo.account.personal.web.dto.request.CreatePersonalAccountTypeDto;
import ru.pshiblo.account.personal.web.dto.response.PersonalResponseDto;
import ru.pshiblo.account.personal.web.dto.response.PersonalTypeResponseDto;

@Mapper(componentModel = "spring")
public interface PersonalAccountMapper {

    @Mapping(target = "validType", expression = "java(account.getType().isValid())")
    @Mapping(target = "typeName", source = "account.type.name")
    @Mapping(target = "typeId", source = "account.type.id")
    @Mapping(target = "number", source = "account.account.number")
    @Mapping(target = "lock", source = "account.account.lock")
    @Mapping(target = "currency", source = "account.account.currency")
    @Mapping(target = "balance", source = "account.account.balance")
    PersonalResponseDto toDto(PersonalAccount account);

    PersonalAccountType personalTypeResponseDtoToPersonalAccountType(PersonalTypeResponseDto personalTypeResponseDto);

    @Mapping(target = "isValid", expression = "java(personalAccountType.isValid())")
    PersonalTypeResponseDto personalAccountTypeToPersonalTypeResponseDto(PersonalAccountType personalAccountType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonalAccountTypeFromPersonalTypeResponseDto(PersonalTypeResponseDto personalTypeResponseDto, @MappingTarget PersonalAccountType personalAccountType);

    PersonalAccountType createPersonalAccountTypeDtoToPersonalAccountType(CreatePersonalAccountTypeDto createPersonalAccountTypeDto);

    CreatePersonalAccountTypeDto personalAccountTypeToCreatePersonalAccountTypeDto(PersonalAccountType personalAccountType);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonalAccountTypeFromCreatePersonalAccountTypeDto(CreatePersonalAccountTypeDto createPersonalAccountTypeDto, @MappingTarget PersonalAccountType personalAccountType);
}
