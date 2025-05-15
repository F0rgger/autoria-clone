package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class DealershipRepositoryTest {

    @Autowired
    private DealershipRepository dealershipRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindById() {
        User admin = new User();
        admin.setEmail("admin@example.com");
        admin = userRepository.save(admin);

        Dealership dealership = new Dealership();
        dealership.setName("AutoSalon Kyiv");
        dealership.setAddress("Kyiv, st. 1");
        dealership.getUsers().add(admin);
        dealership.getUserRoles().put(admin.getId(), "ADMIN");
        dealership = dealershipRepository.save(dealership);

        Optional<Dealership> found = dealershipRepository.findById(dealership.getId());
        assertTrue(found.isPresent());
        assertEquals("AutoSalon Kyiv", found.get().getName());
    }
}