package ru.pshiblo.info.business.web.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class FailedConfirmCompany {
    @NotBlank
    @NotNull
    private String reason;
}
