package ru.pshiblo.info.business.mappers;

import org.mapstruct.Mapper;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyHistory;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.web.dto.*;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    Company toEntity(CompanyResponseDto companyResponseDto);
    CompanyResponseDto toDTO(Company company);
    CompanyUserResponseDto toDTO(CompanyUser companyUser);
    Company toEntity(CreateCompanyDto createCompanyDto);
    Company toEntity(UpdateCompanyDto updateCompanyDto);
    CompanyHistoryDto toDTO(CompanyHistory companyHistory);
}
