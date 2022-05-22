package ru.pshiblo.account.business.clients.model;

import lombok.Data;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.io.Serializable;

@Data
public class Company implements Serializable {
    private Long id;
    private String name;
    private String inn;
    private String address;
    private ConfirmedStatus status;
}
