package ru.pshiblo.transaction.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.transaction.domain.Transaction;
import ru.pshiblo.transaction.domain.TransactionHistory;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "created", ignore = true)
    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "route", source = "route")
    @Mapping(target = "transaction", source = "transaction")
    TransactionHistory toHistory(Transaction transaction, String route);
}
