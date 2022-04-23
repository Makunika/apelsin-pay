package ru.pshiblo.info.business.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.pshiblo.info.business.domain.Company;
import ru.pshiblo.info.business.domain.CompanyHistory;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.web.dto.*;

@Mapper(componentModel = "spring")
public interface CompanyMapper {
    Company companyResponseDtoToCompany(CompanyResponseDto companyResponseDto);
    CompanyResponseDto companyToCompanyResponseDto(Company company);
    CompanyUser companyUserResponseDtoToCompanyUser(CompanyUserResponseDto companyUserResponseDto);
    CompanyUserResponseDto companyUserToCompanyUserResponseDto(CompanyUser companyUser);
    Company createCompanyDtoToCompany(CreateCompanyDto createCompanyDto);
    Company updateCompanyDtoToCompany(UpdateCompanyDto updateCompanyDto);
    CreateCompanyDto companyToCreateCompanyDto(Company company);
    CompanyHistory companyHistoryDtoToCompanyHistory(CompanyHistoryDto companyHistoryDto);
    CompanyHistoryDto companyHistoryToCompanyHistoryDto(CompanyHistory companyHistory);
}
