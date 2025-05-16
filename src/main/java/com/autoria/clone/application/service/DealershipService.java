package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.DealershipRepository;
import com.autoria.clone.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class DealershipService {

    private final DealershipRepository dealershipRepository;
    private final UserRepository userRepository;

    @Transactional
    public Dealership createDealership(Dealership dealership, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (dealership.getUsers() == null) {
            dealership.setUsers(new ArrayList<>());
        }
        dealership.getUsers().add(admin);
        dealership.getUserRoles().put(admin.getId(), "ADMIN");
        return dealershipRepository.save(dealership);
    }

    @Transactional
    public void assignUser(Long dealershipId, Long userId, String role) {
        Dealership dealership = dealershipRepository.findById(dealershipId)
                .orElseThrow(() -> new IllegalArgumentException("Dealership not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (dealership.getUsers() == null) {
            dealership.setUsers(new ArrayList<>());
        }
        dealership.getUsers().add(user);
        dealership.getUserRoles().put(userId, role);
        dealershipRepository.save(dealership);
    }
}