package ru.pshiblo.auth.clients.dto;

import lombok.Data;

@Data
public class CompanyDto {
    private Long id;
    private String name;
    private String inn;
    private String address;
    private String status;
}
