package ru.pshiblo.account.business.clients.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyUser implements Serializable {
    private Long id;
    private long userId;
    private String roleCompany;
    private Company company;
}
