package ru.pshiblo.auth.web.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String email;
    private String phone;
}
