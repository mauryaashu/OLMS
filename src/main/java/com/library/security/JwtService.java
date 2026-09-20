package com.library.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.*;
import java.util.*;

@Service
public class JwtService {
    private final Key key;
    private final long expirationMinutes;

    public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMinutes = expirationMinutes;
    }

    public String generate(String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                   .subject(email)
                   .claim("role", role)
                   .issuedAt(now)
                   .expiration(Date.from(Instant.now().plusSeconds(expirationMinutes * 60)))
                   .signWith(key)
                   .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
                   .setSigningKey(key)
                   .build()
                   .parseClaimsJws(token)
                   .getBody()
                   .getSubject();
    }

    public boolean isValid(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
