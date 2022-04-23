package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.security.enums.ConfirmedStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class CreateCompanyDto implements Serializable {
    @NotNull
    @NotBlank
    private final String name;
    @NotNull
    @Pattern(regexp = "\\d+")
    private final String inn;
    @NotNull
    @NotBlank
    private final String address;
}
