package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Role;
import com.autoria.clone.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    public void testFindByEmail() {
        Role role = new Role();
        role.setName(Role.BUYER);
        role.setPermissions(Arrays.asList("VIEW_ADS", "CONTACT_SELLER"));
        role = roleRepository.save(role);

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Collections.singletonList(role));
        userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("user@example.com");
        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getEmail());
        assertEquals(1, found.get().getRoles().size());
        assertEquals(Role.BUYER, found.get().getRoles().get(0).getName());
        assertTrue(found.get().getRoles().get(0).getPermissions().contains("VIEW_ADS"));
    }

    @Test
    public void testFindByEmailNotFound() {
        Optional<User> found = userRepository.findByEmail("nonexistent@example.com");
        assertTrue(found.isEmpty());
    }
}