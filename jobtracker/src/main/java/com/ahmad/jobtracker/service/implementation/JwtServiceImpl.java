package com.ahmad.jobtracker.service.implementation;

import com.ahmad.jobtracker.entity.User;
import com.ahmad.jobtracker.service.JwtService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        return Jwts.builder()
            .subject(user.getEmail())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expirationMs))
            .signWith(getSecretKey())
            .compact();
    }

    @Override
    public String extractEmail(String token) {
        log.debug("JWT token received, validating...");
        return Jwts.parser()
            .verifyWith(getSecretKey())
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token);
            log.debug("JWT token validated successfully");
            return true;
        } catch (Exception e) {
            log.warn("Invalid JWT token received");
            return false;
        }
    }
}