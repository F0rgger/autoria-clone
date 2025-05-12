package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Сервіс для управління ролями.
 */
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    /**
     * Створює нову роль.
     *
     * @param role Роль
     * @return Створена роль
     */
    @Transactional
    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    /**
     * Ініціалізує базові ролі та дозволи.
     */
    @Transactional
    public void initializeRolesAndPermissions() {
        List<String> roles = Arrays.asList("BUYER", "SELLER", "MANAGER", "ADMIN", "DEALERSHIP");
        List<String> permissions = Arrays.asList(
                "CREATE_ADVERTISEMENT", "VIEW_ANALYTICS", "MODERATE_ADVERTISEMENT",
                "MANAGE_ROLES", "MANAGE_DEALERSHIP_ADS");

        roles.forEach(roleName -> {
            Role role = new Role();
            role.setName(roleName);
            roleRepository.save(role);
        });

        // Приклад прив’язки дозволів
        Role sellerRole = roleRepository.findByName("SELLER").orElseThrow();
        sellerRole.getPermissions().addAll(Arrays.asList("CREATE_ADVERTISEMENT", "VIEW_ANALYTICS"));
        Role dealershipRole = roleRepository.findByName("DEALERSHIP").orElseThrow();
        dealershipRole.getPermissions().add("MANAGE_DEALERSHIP_ADS");
        roleRepository.saveAll(Arrays.asList(sellerRole, dealershipRole));
    }
}