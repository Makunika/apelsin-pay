package ru.pshiblo.transaction.web.dto.request;

import lombok.Data;
import ru.pshiblo.transaction.enums.CardPayType;
import ru.pshiblo.transaction.enums.CardType;
import ru.pshiblo.transaction.enums.Currency;

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
