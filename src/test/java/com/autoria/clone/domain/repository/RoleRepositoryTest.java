package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByName() {
        Role role = new Role();
        role.setName(Role.BUYER);
        role.setPermissions(Arrays.asList("VIEW_ADS", "CONTACT_SELLER"));
        roleRepository.save(role);

        Optional<Role> found = roleRepository.findByName(Role.BUYER);
        assertTrue(found.isPresent());
        assertEquals(Role.BUYER, found.get().getName());
        assertEquals(2, found.get().getPermissions().size());
        assertTrue(found.get().getPermissions().contains("VIEW_ADS"));
    }

    @Test
    public void testFindByNameNotFound() {
        Optional<Role> found = roleRepository.findByName("NON_EXISTENT");
        assertTrue(found.isEmpty());
    }

    @Test
    public void testSaveRoleWithPermissions() {
        Role role = new Role();
        role.setName(Role.ADMIN);
        role.setPermissions(Arrays.asList("MANAGE_USERS", "MANAGE_ADS"));
        Role saved = roleRepository.save(role);

        Optional<Role> found = roleRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(Role.ADMIN, found.get().getName());
        assertEquals(2, found.get().getPermissions().size());
        assertTrue(found.get().getPermissions().contains("MANAGE_USERS"));
    }
}