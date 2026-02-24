package com.microservices.common_service.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@UtilityClass
@Slf4j
public class JwtUtils {

    public String generateToken(String jwtSecretKey, String subject, String id, String issuer, long expirationTimeInMilliSecond) {
        return generateToken(jwtSecretKey, subject, id, issuer, expirationTimeInMilliSecond, new HashMap<>());
    }

    public String generateToken(String jwtSecretKey, String subject, String id, String issuer, long expirationTimeInMilliSecond, Map<String, String> claims) {
        Date issueAt = new Date();
        Date expiryDate = new Date(issueAt.getTime() + expirationTimeInMilliSecond);
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .subject(subject)
                .id(id)
                .issuedAt(issueAt)
                .issuer(issuer)
                .expiration(expiryDate)
                .claims(claims)
                .signWith(key).compact();
    }

    public Claims extractClaims(String jwtSecretKey, String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public String getSubject(String jwtSecretKey, String token) {
        try {
            return extractClaims(jwtSecretKey, token).getSubject();
        } catch (ExpiredJwtException ex) {
            log.error("JwtUtils: getSubject: {}", ex.getMessage());
            return null;
        }
    }

    public boolean isValidToken(String jwtSecretKey, String token, String subject) {
        try {
            String extractedSubject = JwtUtils.getSubject(jwtSecretKey, token);
            return (extractedSubject.equals(subject) && !isTokenExpired(jwtSecretKey, token));
        } catch (ExpiredJwtException ex) {
            log.error("JwtUtils: isValidToken: {}", ex.getMessage());
            return false;
        }
    }

    public boolean isTokenExpired(String jwtSecretKey, String token) {
        try {
            return extractClaims(jwtSecretKey, token).getExpiration().before(new Date());
        } catch (ExpiredJwtException ex) {
            log.error("JwtUtils: isTokenExpired: {}", ex.getMessage());
            return true;
        }
    }
}
