package org.charityaid.charity_aid.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    private static final String MFA_CHALLENGE_CLAIM = "mfa_challenge";
    private static final long MFA_TOKEN_EXPIRY_MS = 5 * 60 * 1000L; // 5 minutes

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateToken(String email, String role) {
        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * FR-11: Generate a short-lived (5-minute) challenge token for MFA verification.
     * This token is NOT a full auth token — it only identifies the user mid-login.
     */
    public String generateMfaChallengeToken(String email) {
        return Jwts.builder()
                .subject(email)
                .claim(MFA_CHALLENGE_CLAIM, true)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + MFA_TOKEN_EXPIRY_MS))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * FR-11: Returns true if the token is a valid, unexpired MFA challenge token.
     */
    public boolean isMfaChallengeToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return Boolean.TRUE.equals(claims.get(MFA_CHALLENGE_CLAIM, Boolean.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
