package ru.pshiblo.info.personal.web.dto;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class ConfirmedPersonInfoDto {
    @Size(min = 6, max = 6)
    private String passportNumber;
    @Size(min = 4, max = 4)
    private String passportSeries;
    private int personInfoId;
}
