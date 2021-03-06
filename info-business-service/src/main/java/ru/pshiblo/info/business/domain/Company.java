package ru.pshiblo.info.business.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Where;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.pshiblo.security.enums.ConfirmedStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
@RequiredArgsConstructor
@Where(clause = "is_deleted = false")
public class Company {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String inn;
    private String address;
    private ConfirmedStatus status;
    private String apiKey;

    @CreatedDate
    private LocalDateTime created;
    @LastModifiedDate
    private LocalDateTime updated;
    @CreatedBy
    private String createdBy;
    @LastModifiedBy
    private String updatedBy;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @OneToMany(mappedBy = "company", orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CompanyUser> companyUsers = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Company company = (Company) o;
        return id != null && Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
