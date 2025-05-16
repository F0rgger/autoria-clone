package com.autoria.clone.config;

import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtSecretValidator {

    private static final Logger logger = LoggerFactory.getLogger(JwtSecretValidator.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    public void validateJwtSecret() {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(jwtSecret);
            if (decodedKey.length < 64) {
                throw new IllegalStateException("JWT secret key is too short for HS512. Required: >=512 bits (64 bytes), got: " + (decodedKey.length * 8) + " bits");
            }
            Keys.hmacShaKeyFor(decodedKey);
            logger.info("JWT secret is valid, length: {} bytes", decodedKey.length);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid JWT secret: {}", e.getMessage());
            throw new IllegalStateException("Invalid JWT secret", e);
        }
    }
}