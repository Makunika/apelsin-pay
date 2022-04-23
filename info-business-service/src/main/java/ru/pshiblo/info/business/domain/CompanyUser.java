package ru.pshiblo.info.business.domain;

import lombok.*;
import org.hibernate.Hibernate;
import ru.pshiblo.info.business.enums.RoleCompany;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class CompanyUser {
    @Id
    @GeneratedValue
    private Long id;
    private long userId;
    private RoleCompany roleCompany;
    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CompanyUser that = (CompanyUser) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
