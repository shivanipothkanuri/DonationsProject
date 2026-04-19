package org.charityaid.charity_aid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FR-11: Request body for the MFA verification step.
 * Sent after a login that returned mfaRequired=true.
 */
@Getter
@NoArgsConstructor
public class MfaVerifyRequest {

    /** Short-lived MFA challenge JWT returned from the login endpoint. */
    @NotBlank(message = "MFA token is required")
    private String mfaToken;

    /** 6-digit TOTP code from the authenticator app. */
    @NotBlank(message = "TOTP code is required")
    @Pattern(regexp = "\\d{6}", message = "TOTP code must be exactly 6 digits")
    private String totpCode;
}
