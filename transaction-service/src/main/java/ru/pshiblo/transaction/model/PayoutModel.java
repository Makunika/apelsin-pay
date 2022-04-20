package ru.pshiblo.transaction.model;

import lombok.Data;

@Data
public class PayoutModel {
    private String name;
    private String inn;
    private String kpp;
    private String bik;
    private String bankName;
    private String corrAccountNumber;
    private String accountNumber;
}
