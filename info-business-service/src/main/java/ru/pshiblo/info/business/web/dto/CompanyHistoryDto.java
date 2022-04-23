package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class CompanyHistoryDto implements Serializable {
    private final LocalDateTime created;
    private final LocalDateTime updated;
    private final String createdBy;
    private final String updatedBy;
    private final ConfirmedStatus status;
    private final String reason;
    private final String name;
    private final String inn;
    private final String address;
}
