package ru.pshiblo.transaction.web.dto;

import lombok.Data;
import ru.pshiblo.transaction.enums.CardPayType;
import ru.pshiblo.transaction.enums.CardType;
import ru.pshiblo.transaction.enums.Currency;

/**
 * @author Maxim Pshiblo
 */
@Data
public class CreateDepositCardDto {
    private CardType cardType;
    private CardPayType cardPayType;
    private Currency currency;
}
