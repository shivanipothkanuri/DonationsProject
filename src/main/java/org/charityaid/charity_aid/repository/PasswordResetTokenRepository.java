package org.charityaid.charity_aid.repository;

import java.util.Optional;

import org.charityaid.charity_aid.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    Optional<PasswordResetToken> findByToken(String token);

    // Invalidate all previous unused tokens for a user before issuing a new one
    @Modifying
    @Query("UPDATE PasswordResetToken t SET t.used = true WHERE t.user.userId = :userId AND t.used = false")
    void invalidateAllForUser(@Param("userId") Integer userId);
}
