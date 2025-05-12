package com.autoria.clone.api;

import com.autoria.clone.application.service.DealershipService;
import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Контролер для управління автосалонами.
 */
@RestController
@RequestMapping("/dealerships")
@RequiredArgsConstructor
public class DealershipController {

    private final DealershipService dealershipService;

    /**
     * Створює новий автосалон.
     *
     * @param dealership Автосалон
     * @param adminId ID адміністратора автосалону
     * @return Створений автосалон
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Dealership> createDealership(
            @Valid @RequestBody Dealership dealership,
            @RequestParam Long adminId) {
        User admin = new User();
        admin.setId(adminId);
        Dealership created = dealershipService.createDealership(dealership, admin);
        return ResponseEntity.ok(created);
    }
}