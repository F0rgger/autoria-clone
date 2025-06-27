package com.autoria.clone.api;

import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.UserRepository;
import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/api/premium")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @PostMapping("/purchase")
    public ResponseEntity<String> purchasePremium(@RequestParam String email) {
        if (email == null || !email.contains("@")) {
            return ResponseEntity.badRequest().body("Invalid email");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (user.isPremium()) {
            logger.warn("User {} is already a premium member", email);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User is already a premium member");
        }
        user.setPremium(true);
        Role sellerRole = roleRepository.findByName(Role.SELLER)
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName(Role.SELLER);
                    newRole.setPermissions(Arrays.asList("CREATE_ADVERTISEMENT", "EDIT_ADVERTISEMENT", "VIEW_ADVERTISEMENT_STATS"));
                    return roleRepository.save(newRole);
                });
        user.getRoles().add(sellerRole);
        userRepository.save(user);
        logger.info("User {} upgraded to premium (mocked)", email);
        return ResponseEntity.ok("User upgraded to premium successfully (mocked)");
    }
}