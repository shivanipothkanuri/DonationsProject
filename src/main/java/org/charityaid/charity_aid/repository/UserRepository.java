package org.charityaid.charity_aid.repository;

import java.util.List;
import java.util.Optional;

import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailAddress(String emailAddress);

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByUserRole(UserRole role);

    List<User> findByUserRole(UserRole role);

    List<User> findByUserRoleAndAccountStatus(UserRole role, AccountStatus status);

    long countByAccountStatus(AccountStatus status);
}
