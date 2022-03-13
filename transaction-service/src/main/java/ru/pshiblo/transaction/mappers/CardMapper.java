package ru.pshiblo.transaction.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.transaction.domain.Card;
import ru.pshiblo.transaction.web.dto.request.CreateCardDto;
import ru.pshiblo.transaction.web.dto.response.CardResponseDto;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "pin", ignore = true)
    @Mapping(target = "number", ignore = true)
    @Mapping(target = "lockCard", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "expired", ignore = true)
    @Mapping(target = "cvc", ignore = true)
    @Mapping(target = "account", ignore = true)
    Card toEntity(CreateCardDto card);

    @Mapping(target = "balance", source = "account.balance")
    @Mapping(target = "accountNumber", source = "account.number")
    CardResponseDto toDto(Card card);

    Collection<CardResponseDto> toDtos(Collection<Card> card);
}
