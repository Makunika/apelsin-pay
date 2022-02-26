package ru.pshiblo.account.web.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserServerDto {
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
    private LocalDate birthday;
    private Integer passportSeries;
    private Integer passportNumber;
}
