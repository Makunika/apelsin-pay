package ru.pshiblo.transaction.web.dto.response;

import lombok.Data;
import ru.pshiblo.transaction.enums.CardPayType;
import ru.pshiblo.transaction.enums.CardType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CardResponseDto {
    private String number;
    private CardPayType typePay;
    private CardType type;
    private boolean lockCard;
    private int userId;
    private LocalDate expired;
    private String accountNumber;
    private BigDecimal balance;
}
