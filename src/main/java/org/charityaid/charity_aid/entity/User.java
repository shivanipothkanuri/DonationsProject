package org.charityaid.charity_aid.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_ID")
    private Integer userId;

    @Column(name = "FULL_NAME", nullable = false, length = 100)
    private String fullName;

    @Column(name = "EMAIL_ADDRESS", nullable = false, unique = true, length = 150)
    private String emailAddress;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "PHONE_NUMBER", length = 20)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "USER_ROLE", nullable = false, length = 30)
    private UserRole userRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_STATUS", nullable = false, length = 20)
    private AccountStatus accountStatus;

    @Column(name = "CREATED_DATE", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "LAST_LOGIN")
    private LocalDateTime lastLogin;

    // Counts consecutive failed logins. 5+ = account locked (FR-09)
    @Column(name = "FAILED_LOGIN_COUNT", nullable = false)
    private int failedLoginCount;

    // FR-11: Multi-Factor Authentication fields (only used for ADMINISTRATOR role)
    @Column(name = "MFA_ENABLED", nullable = false)
    private boolean mfaEnabled;

    @Column(name = "MFA_SECRET", length = 100)
    private String mfaSecret;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
        if (accountStatus == null) {
            accountStatus = AccountStatus.ACTIVE;
        }
        failedLoginCount = 0;
    }
}
