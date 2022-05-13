package ru.pshiblo.transaction.mappers;

import org.mapstruct.*;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.domain.TransactionHistory;
import ru.pshiblo.transaction.web.dto.response.TransactionResponseDto;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "route", source = "route")
    @Mapping(target = "transaction", source = "transaction")
    TransactionHistory toHistory(Transaction transaction, String route);
    TransactionResponseDto toDTO(Transaction transaction);
}
