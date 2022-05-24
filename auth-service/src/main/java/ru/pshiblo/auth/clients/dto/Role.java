package ru.pshiblo.auth.clients.dto;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Immutable;

import javax.persistence.*;
import java.util.Objects;

@Data
public class Role {
    private Integer id;
    private String name;
}