package ru.pshiblo.card.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.CardPayType;
import ru.pshiblo.account.enums.CardType;
import ru.pshiblo.account.enums.Currency;

/**
 * @author Maxim Pshiblo
 */
@Data
public class CreateCardDto {
    private CardType type;
    private CardPayType typePay;
    private Currency currency;
    private int userId;
}
