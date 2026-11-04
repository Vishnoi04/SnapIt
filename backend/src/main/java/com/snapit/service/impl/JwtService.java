package com.snapit.service.impl;

import com.snapit.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Value("${snapit.jwt.secret}")
    private String jwtSecret;
    @Value("${snapit.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(User user) {
        Date issuedDate = new Date();
        Date expiredBy = new Date(issuedDate.getTime() + jwtExpirationMs);
        return Jwts.builder().subject(user.getEmail()).claim("userId", user.getId())
                .claim("role", user.getRole().name()).issuedAt(issuedDate).expiration(expiredBy)
                .signWith(getSigningKey()).compact();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        Claims claims = extractAllClaims(token);
        String email = claims.getSubject();
        Date expiration = claims.getExpiration();
        Object tokenUserId = claims.get("userId");
        Object tokenRole = claims.get("role");
        return email != null
                && email.equals(user.getEmail())
                && expiration != null
                && expiration.after(new Date())
                && tokenUserId instanceof Number userId
                && user.getId() != null
                && userId.longValue() == user.getId()
                && tokenRole instanceof String role
                && user.getRole() != null
                && role.equals(user.getRole().name());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
