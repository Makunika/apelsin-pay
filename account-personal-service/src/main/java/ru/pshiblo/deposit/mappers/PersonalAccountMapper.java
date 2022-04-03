package ru.pshiblo.deposit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.deposit.core.domain.PersonalAccount;
import ru.pshiblo.deposit.web.dto.response.PersonalResponseDto;

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
}
