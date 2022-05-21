package ru.pshiblo.info.business.web.dto;

import lombok.Data;
import ru.pshiblo.security.enums.ConfirmedStatus;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
public class UpdateCompanyDto implements Serializable {
    @NotNull
    private Long id;
    @NotNull
    @NotBlank
    private String name;
    @NotNull
    @Pattern(regexp = "\\d+")
    private String inn;
    @NotNull
    @NotBlank
    private String address;
}
