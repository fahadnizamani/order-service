package com.example.order_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class OrderJwtValidator {

    @Value("${jwt.secret}")
    private String secret;

    public String validateAndExtractEmail(String authorizationHeader, String xUserEmailHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header"
            );
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing JWT token"
            );
        }

        if (xUserEmailHeader == null || xUserEmailHeader.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing X-User-Email header"
            );
        }

        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            if (claims.getExpiration() == null || claims.getExpiration().before(new Date())) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "JWT has expired"
                );
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid or expired JWT"
            );
        }

        String subject = claims.getSubject();
        if (subject == null || subject.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "JWT subject missing"
            );
        }

        if (!subject.equalsIgnoreCase(xUserEmailHeader.trim())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User identity mismatch"
            );
        }

        return subject;
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
