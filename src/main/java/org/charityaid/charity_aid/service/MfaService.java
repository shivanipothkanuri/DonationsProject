package org.charityaid.charity_aid.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Service;

import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

/**
 * FR-11: TOTP-based Multi-Factor Authentication service.
 * Generates Base32 secrets, builds otpauth:// URIs, and verifies TOTP codes.
 */
@Service
public class MfaService {

    private static final String ISSUER = "CharityAid";

    /**
     * Generate a new Base32-encoded TOTP secret (160-bit).
     */
    public String generateSecret() {
        return new DefaultSecretGenerator().generate();
    }

    /**
     * Build an otpauth:// URI that encodes all parameters needed for the
     * authenticator app. Pass this to a QR-code renderer on the frontend.
     */
    public String buildOtpauthUrl(String userEmail, String secret) {
        String encodedIssuer = URLEncoder.encode(ISSUER, StandardCharsets.UTF_8);
        String encodedLabel = URLEncoder.encode(ISSUER + ":" + userEmail, StandardCharsets.UTF_8);
        return "otpauth://totp/" + encodedLabel
                + "?secret=" + secret
                + "&issuer=" + encodedIssuer
                + "&algorithm=SHA1&digits=6&period=30";
    }

    /**
     * Verify a 6-digit TOTP code against the stored Base32 secret.
     * Allows ±1 time-step tolerance to account for clock drift.
     */
    public boolean verifyCode(String secret, String code) {
        if (secret == null || code == null || code.length() != 6) {
            return false;
        }
        try {
            CodeVerifier verifier = new DefaultCodeVerifier(
                    new DefaultCodeGenerator(),
                    new SystemTimeProvider());
            return verifier.isValidCode(secret, code);
        } catch (Exception e) {
            return false;
        }
    }
}
