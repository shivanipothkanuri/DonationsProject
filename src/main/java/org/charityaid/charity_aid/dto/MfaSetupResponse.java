package org.charityaid.charity_aid.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * FR-11: Returned after initiating MFA setup.
 * Contains the Base32 TOTP secret and the otpauth:// URL to display as a QR code.
 */
@Getter
@Builder
public class MfaSetupResponse {

    /** Raw Base32 TOTP secret — display to user for manual entry. */
    private String secret;

    /** otpauth:// URI — pass to a QR-code library to render the QR image. */
    private String otpauthUrl;
}
