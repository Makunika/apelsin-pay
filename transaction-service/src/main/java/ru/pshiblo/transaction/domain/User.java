package ru.pshiblo.transaction.domain;

import lombok.Data;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Maxim Pshiblo
 */
@Table(name = "v_users")
@Entity
@Data
@Immutable
public class User {
    @Id
    private Integer id;
    private String email;
    private String phone;
}
