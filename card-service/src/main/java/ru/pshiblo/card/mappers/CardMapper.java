package ru.pshiblo.card.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.pshiblo.account.domain.Card;
import ru.pshiblo.card.web.dto.request.CreateCardDto;
import ru.pshiblo.card.web.dto.response.CardResponseDto;

import java.util.Collection;

@Mapper(componentModel = "spring")
public interface CardMapper {

    @Mapping(target = "updated", ignore = true)
    @Mapping(target = "created", ignore = true)
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
