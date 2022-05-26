package ru.pshiblo.users.domain;

import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Table(name = "remember_password")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class RememberPassword {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    @CreatedDate
    private LocalDateTime created;
    private Integer untilSeconds;
    private boolean isValid = true;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RememberPassword that = (RememberPassword) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
