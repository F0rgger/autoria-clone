package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Dealership;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.DealershipRepository;
import com.autoria.clone.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DealershipServiceTest {

    @Autowired
    private DealershipService dealershipService;

    @MockBean
    private DealershipRepository dealershipRepository;

    @MockBean
    private UserRepository userRepository;

    @Test
    public void testCreateDealershipSuccess() {
        Dealership dealership = new Dealership();
        User admin = new User();
        admin.setId(1L);
        when(userRepository.findById(1L)).thenReturn(java.util.Optional.of(admin));
        when(dealershipRepository.save(any(Dealership.class))).thenReturn(dealership);

        Dealership result = dealershipService.createDealership(dealership, 1L);
        verify(dealershipRepository).save(dealership);
        assertEquals(1, dealership.getUsers().size());
    }
}