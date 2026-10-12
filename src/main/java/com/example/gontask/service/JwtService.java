package com.example.gontask.service;

import com.example.gontask.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (secretBytes.length < 32) {
            throw new IllegalStateException(
                    "app.jwt.secret (JWT_SECRET) must be at least 32 characters long"
            );
        }

        this.key = Keys.hmacShaKeyFor(secretBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(Account account) {

        return Jwts.builder()
                .subject(account.getEmail())
                .claim("role", account.getRole().name())
                .issuedAt(new Date())
                .expiration(
                        new Date(System.currentTimeMillis() + expirationMs)
                )
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    public boolean isTokenValid(String token) {

        try {
            extractAllClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
