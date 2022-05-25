package ru.pshiblo.transaction.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class PayoutModel {
    @NotNull
    private Boolean isPerson;
    private String name;
    @NotNull
    @NotBlank
    private String inn;
    private String kpp;
    @NotNull
    @NotBlank
    private String bik;
    @NotNull
    @NotBlank
    private String bankName;
    @NotNull
    @NotBlank
    private String corrAccountNumber;
    @NotNull
    @NotBlank
    private String accountNumber;
}
