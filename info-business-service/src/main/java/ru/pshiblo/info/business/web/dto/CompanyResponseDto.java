package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.io.Serializable;

@Data
public class CompanyResponseDto implements Serializable {
    private final Long id;
    private final String name;
    private final String inn;
    private final String address;
    private final ConfirmedStatus status;
}
