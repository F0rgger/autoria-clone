package com.autoria.clone.api;

import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Key jwtSigningKey;
    private final long jwtExpiration;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                          @Value("${jwt.secret}") String jwtSecret,
                          @Value("${jwt.expiration}") long jwtExpiration) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        this.jwtExpiration = jwtExpiration;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        logger.info("Received registration request for email: {}", user.getEmail());

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("User with email {} already exists", user.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + user.getEmail() + " already exists");
        }

        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            logger.info("User registered successfully: {}", user.getEmail());
            return ResponseEntity.ok("Пользователь зарегистрирован");
        } catch (DataIntegrityViolationException e) {
            logger.error("Data integrity violation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + user.getEmail() + " already exists");
        } catch (Exception e) {
            logger.error("Unexpected error registering user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error registering user: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User loginUser) {
        logger.info("Received login request for email: {}", loginUser.getEmail());
        User user = userRepository.findByEmail(loginUser.getEmail())
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", loginUser.getEmail());
                    return new RuntimeException("Пользователь не найден");
                });
        if (passwordEncoder.matches(loginUser.getPassword(), user.getPassword())) {
            try {
                String token = Jwts.builder()
                        .setSubject(user.getEmail())
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                        .signWith(jwtSigningKey, SignatureAlgorithm.HS512)
                        .compact();
                logger.info("Login successful for user: {}", user.getEmail());
                return ResponseEntity.ok(token);
            } catch (JwtException e) {
                logger.error("JWT creation failed: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to generate token: " + e.getMessage());
            }
        }
        logger.warn("Invalid credentials for user: {}", loginUser.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Неверные учетные данные");
    }
}