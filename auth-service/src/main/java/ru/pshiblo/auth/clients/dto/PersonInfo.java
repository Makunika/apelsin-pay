package ru.pshiblo.auth.clients.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonInfo {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private LocalDate birthday;
    private Integer passportSeries;
    private Integer passportNumber;
    private String status;
    private Boolean lock;
}
