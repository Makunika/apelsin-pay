package ru.pshiblo.account.business.clients.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyUser implements Serializable {
    private final Long id;
    private final long userId;
    private final String roleCompany;
    private final Company company;
}
