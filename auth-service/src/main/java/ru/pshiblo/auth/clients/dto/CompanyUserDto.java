package ru.pshiblo.auth.clients.dto;

import lombok.Data;

@Data
public class CompanyUserDto {
    private Long id;
    private long userId;
    private String roleCompany;
    private CompanyDto company;
}
