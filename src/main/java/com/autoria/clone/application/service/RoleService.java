package com.autoria.clone.application.service;



import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.repository.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Transactional
    public void initializeRolesAndPermissions() {
        List<String> roleNames = Arrays.asList(
                Role.BUYER, Role.SELLER, Role.MANAGER, Role.ADMIN,
                Role.DEALERSHIP, Role.SALES, Role.MECHANIC
        );

        for (String roleName : roleNames) {

            if (roleRepository.findByName(roleName).isEmpty()) {
                Role role = new Role();
                role.setName(roleName);


                switch (roleName) {
                    case Role.BUYER -> role.setPermissions(Arrays.asList("VIEW_ADS", "CONTACT_SELLER"));
                    case Role.SELLER -> role.setPermissions(Arrays.asList("CREATE_ADS", "EDIT_ADS"));
                    case Role.MANAGER -> role.setPermissions(Arrays.asList("MODERATE_ADS"));
                    case Role.ADMIN -> role.setPermissions(Arrays.asList("MANAGE_ROLES", "MODERATE_ADVERTISEMENT"));
                    case Role.DEALERSHIP -> role.setPermissions(Arrays.asList("MANAGE_STAFF"));
                    case Role.SALES -> role.setPermissions(Arrays.asList("SELL_CARS"));
                    case Role.MECHANIC -> role.setPermissions(Arrays.asList("SERVICE_CARS"));
                }

                roleRepository.save(role);
            }
        }
    }
}