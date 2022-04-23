package ru.pshiblo.auth.clients.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PersonInfoDto implements Serializable {
    private final Long id;
    private final Long userId;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final String email;
    private final String phone;
    private final LocalDate birthday;
    private final Integer passportSeries;
    private final Integer passportNumber;
    private final String status;
}
