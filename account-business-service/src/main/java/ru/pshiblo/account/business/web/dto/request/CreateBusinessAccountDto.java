package ru.pshiblo.account.business.web.dto.request;

import lombok.Data;
import ru.pshiblo.account.enums.Currency;

@Data
public class CreateBusinessAccountDto {
    private long typeId;
    private long companyId;
    private long userId;
}
