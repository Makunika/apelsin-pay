package ru.pshiblo.info.personal.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class ConfirmedPersonInfoDto {
    @Range(max = 9999)
    private int passportNumber;
    @Range(max = 999999)
    private int passportSeries;
    private int personInfoId;
}
