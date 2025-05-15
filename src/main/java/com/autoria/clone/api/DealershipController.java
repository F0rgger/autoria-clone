package com.autoria.clone.api;

import com.autoria.clone.application.dto.DealershipDTO;
import com.autoria.clone.application.mapper.EntityMapper;
import com.autoria.clone.application.service.DealershipService;
import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dealerships")
@RequiredArgsConstructor
public class DealershipController {

    private final DealershipService dealershipService;
    private final EntityMapper entityMapper;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DealershipDTO> createDealership(
            @Valid @RequestBody DealershipDTO dealershipDTO,
            @RequestParam Long adminId) {
        Dealership dealership = entityMapper.toDealershipEntity(dealershipDTO);
        Dealership created = dealershipService.createDealership(dealership, adminId);

        List<User> users = dealershipDTO.getUserIds().stream()
                .filter(userId -> userId != null)
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId)))
                .collect(Collectors.toList());
        if (created.getUsers() == null) {
            created.setUsers(new ArrayList<>()); // Безопасная инициализация
        }
        created.getUsers().addAll(users); // Используем addAll для добавления списка

        return ResponseEntity.ok(entityMapper.toDealershipDTO(created));
    }

    @PostMapping("/{id}/assign-user")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> assignUserToDealership(@PathVariable Long id, @RequestParam Long userId, @RequestParam String role) {
        if (id == null || userId == null) {
            return ResponseEntity.badRequest().body("ID or userId must not be null");
        }
        dealershipService.assignUser(id, userId, role);
        return ResponseEntity.ok("User assigned to dealership");
    }
}