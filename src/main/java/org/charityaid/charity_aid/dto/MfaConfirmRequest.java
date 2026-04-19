package org.charityaid.charity_aid.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * FR-11: Request body for the MFA confirmation step (POST /api/auth/mfa/confirm).
 * Administrator submits the first TOTP code after setting up the authenticator app.
 */
@Getter
@NoArgsConstructor
public class MfaConfirmRequest {

    /** 6-digit TOTP code from the authenticator app, used to confirm enrollment. */
    @NotBlank(message = "TOTP code is required")
    @Pattern(regexp = "\\d{6}", message = "TOTP code must be exactly 6 digits")
    private String totpCode;
}
