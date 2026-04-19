package org.charityaid.charity_aid.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponse {

    private String token;
    private String tokenType;
    private Integer userId;
    private String fullName;
    private String email;
    private String role;
    private long expiresIn;

    // FR-11: MFA challenge fields — set when administrator login requires a second factor
    /** True when the user must complete an MFA challenge before receiving a full JWT. */
    private boolean mfaRequired;
    /** Short-lived challenge token to pass to POST /api/auth/mfa/verify (only set when mfaRequired=true). */
    private String mfaToken;
}
