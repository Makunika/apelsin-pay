package ru.pshiblo.account.business.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.account.business.domain.BusinessAccount;
import ru.pshiblo.account.business.web.dto.response.BusinessAccountResponseDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BusinessAccountMapper {
    @Mapping(target = "currency", source = "account.type.currency")
    @Mapping(target = "typeName", source = "account.type.name")
    @Mapping(target = "typeId", source = "account.type.id")
    @Mapping(target = "number", source = "account.account.number")
    @Mapping(target = "lock", source = "account.account.lock")
    @Mapping(target = "balance", source = "account.account.balance")
    BusinessAccountResponseDto toDto(BusinessAccount account);

    List<BusinessAccountResponseDto> toDtos(List<BusinessAccount> accounts);
}
