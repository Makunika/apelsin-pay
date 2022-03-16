package ru.pshiblo.deposit.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.account.domain.Deposit;
import ru.pshiblo.deposit.web.dto.request.CreateDepositDto;
import ru.pshiblo.deposit.web.dto.response.DepositResponseDto;

@Mapper(componentModel = "spring")
public interface DepositMapper {

    @Mapping(target = "validDepositType",  expression = "java(deposit.getDepositType().isValid())")
    @Mapping(target = "requiredToPayment", expression = "java(deposit.getStartDepositDate() == null)")
    @Mapping(target = "depositTypeId", source = "deposit.depositType.id")
    @Mapping(target = "currency", source = "deposit.depositType.currency")
    @Mapping(target = "balance", source = "deposit.account.balance")
    DepositResponseDto toDto(Deposit deposit);

    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "startDepositDate", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "depositType", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "account", ignore = true)
    Deposit toEntity(CreateDepositDto createDepositDto);
}
