package ru.pshiblo.account.business.mappers;

import org.mapstruct.*;
import ru.pshiblo.account.business.domain.BusinessAccountType;
import ru.pshiblo.account.business.web.dto.request.CreateBusinessAccountTypeDto;
import ru.pshiblo.account.business.web.dto.response.BusinessAccountTypeResponseDto;

@Mapper(componentModel = "spring")
public interface BusinessAccountTypeMapper {
    BusinessAccountType toEntity(CreateBusinessAccountTypeDto createBusinessAccountTypeDto);
    @Mapping(target = "isValid", expression = "java(businessAccountType.isValid())")
    BusinessAccountTypeResponseDto toDTO(BusinessAccountType businessAccountType);
}
