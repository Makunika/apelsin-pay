package ru.pshiblo.card.web.dto.request;

import lombok.Data;

@Data
public class ChangePinCardDto {
    private String newPin;
    private String numberCard;
}
