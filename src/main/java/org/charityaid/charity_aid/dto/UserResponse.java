package org.charityaid.charity_aid.dto;

import java.time.LocalDateTime;

import org.charityaid.charity_aid.entity.AccountStatus;
import org.charityaid.charity_aid.entity.User;
import org.charityaid.charity_aid.entity.UserRole;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private Integer userId;
    private String fullName;
    private String emailAddress;
    private String phoneNumber;
    private UserRole userRole;
    private AccountStatus accountStatus;
    private LocalDateTime createdDate;
    private LocalDateTime lastLogin;
    private int failedLoginCount;
    private boolean notificationsEnabled;

    public static UserResponse from(User u) {
        return UserResponse.builder()
                .userId(u.getUserId())
                .fullName(u.getFullName())
                .emailAddress(u.getEmailAddress())
                .phoneNumber(u.getPhoneNumber())
                .userRole(u.getUserRole())
                .accountStatus(u.getAccountStatus())
                .createdDate(u.getCreatedDate())
                .lastLogin(u.getLastLogin())
                .failedLoginCount(u.getFailedLoginCount())
                .notificationsEnabled(u.isNotificationsEnabled())
                .build();
    }
}
