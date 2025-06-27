package com.autoria.clone.api;

import com.autoria.clone.application.dto.UserDTO;
import com.autoria.clone.application.mapper.EntityMapper;
import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.RoleRepository;
import com.autoria.clone.domain.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final Key jwtSigningKey;
    private final long jwtExpiration;
    private final EntityMapper entityMapper;

    public AuthController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
                          @Value("${jwt.secret}") String jwtSecret,
                          @Value("${jwt.expiration}") long jwtExpiration,
                          EntityMapper entityMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtSigningKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
        this.jwtExpiration = jwtExpiration;
        this.entityMapper = entityMapper;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Received registration request for email: {}", userDTO.getEmail());
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            logger.warn("User with email {} already exists", userDTO.getEmail());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email " + userDTO.getEmail() + " already exists");
        }

        User user = entityMapper.toUserEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setAdvertisementCount(0);

        Role role;
        if (userDTO.getEmail().toLowerCase().contains("admin")) {
            role = roleRepository.findByName(Role.ADMIN)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(Role.ADMIN);
                        newRole.setPermissions(Arrays.asList(
                                "MANAGE_ROLES",
                                "MODERATE_ADVERTISEMENT",
                                "EDIT_ADVERTISEMENT",
                                "CREATE_ADVERTISEMENT",
                                "VIEW_ADVERTISEMENT_STATS"
                        ));
                        return roleRepository.save(newRole);
                    });
        } else {
            role = roleRepository.findByName(Role.BUYER)
                    .orElseGet(() -> {
                        Role newRole = new Role();
                        newRole.setName(Role.BUYER);
                        newRole.setPermissions(Arrays.asList("VIEW_ADS", "CONTACT_SELLER"));
                        return roleRepository.save(newRole);
                    });
        }
        user.getRoles().add(role);

        userRepository.save(user);
        logger.info("User registered successfully: {}", user.getEmail());
        return ResponseEntity.ok("Пользователь зарегистрирован");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserDTO loginUserDTO) {
        logger.info("Received login request for email: {}", loginUserDTO.getEmail());
        User user = userRepository.findByEmail(loginUserDTO.getEmail())
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", loginUserDTO.getEmail());
                    return new RuntimeException("Пользователь не найден");
                });
        if (passwordEncoder.matches(loginUserDTO.getPassword(), user.getPassword())) {
            try {
                String token = generateJwtToken(user);
                logger.info("Login successful for user: {}", user.getEmail());
                return ResponseEntity.ok(token);
            } catch (JwtException e) {
                logger.error("JWT creation failed: {}", e.getMessage(), e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to generate token: " + e.getMessage());
            }
        }
        logger.warn("Invalid credentials for user: {}", loginUserDTO.getEmail());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Неверные учетные данные");
    }

    @PostMapping("/create-manager")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createManager(@Valid @RequestBody UserDTO userDTO) {

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("User with email " + userDTO.getEmail() + " already exists");
        }


        Role managerRole = roleRepository.findByName(Role.MANAGER)
                .orElseGet(() -> {
                    Role newManagerRole = new Role();
                    newManagerRole.setName("MANAGER");
                    return roleRepository.save(newManagerRole);
                });

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setPremium(false);
        user.setAdvertisementCount(0);
        user.getRoles().add(managerRole);

        userRepository.save(user);
        return ResponseEntity.ok("Manager created successfully");
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching current user: {}", userDetails.getUsername());
        if (userDetails == null) {
            logger.error("UserDetails is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + userDetails.getUsername()));
        return ResponseEntity.ok(Map.of("id", user.getId(), "email", user.getEmail()));
    }

    @PostMapping("/upgrade")
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<String> upgradeAccount(@RequestParam String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Role sellerRole = roleRepository.findByName(Role.SELLER)
                .map(role -> {
                    role.setPermissions(Arrays.asList(
                            "CREATE_ADVERTISEMENT",
                            "EDIT_ADVERTISEMENT",
                            "DELETE_ADVERTISEMENT",
                            "VIEW_ADVERTISEMENT_STATS"
                    ));
                    return roleRepository.save(role);
                })
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Role.SELLER);
                    newRole.setPermissions(Arrays.asList(
                            "CREATE_ADVERTISEMENT",
                            "EDIT_ADVERTISEMENT",
                            "DELETE_ADVERTISEMENT",
                            "VIEW_ADVERTISEMENT_STATS"
                    ));
                    return roleRepository.save(newRole);
                });
        user.getRoles().add(sellerRole);
        user.setPremium(true);
        userRepository.save(user);
        return ResponseEntity.ok("Account upgraded to premium");
    }

    private String generateJwtToken(User user) {
        String roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));
        String permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .collect(Collectors.joining(","));

        logger.debug("Generating JWT for user: {}, roles: {}, permissions: {}", user.getEmail(), roles, permissions);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("roles", roles)
                .claim("permissions", permissions)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(jwtSigningKey, SignatureAlgorithm.HS512)
                .compact();
    }
}