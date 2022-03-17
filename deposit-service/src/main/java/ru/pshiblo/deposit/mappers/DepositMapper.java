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
}
