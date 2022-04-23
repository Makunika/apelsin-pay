package ru.pshiblo.info.business.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.pshiblo.info.business.domain.CompanyUser;
import ru.pshiblo.info.business.enums.RoleCompany;
import ru.pshiblo.security.enums.ConfirmedStatus;

import java.util.List;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, Long> {
    List<CompanyUser> findByUserId(long userId);

    List<CompanyUser> findByUserIdAndRoleCompany(long userId, RoleCompany roleCompany);
}