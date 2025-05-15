package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import com.autoria.clone.domain.repository.AdvertisementRepository;
import com.autoria.clone.infrastructure.service.EmailService;
import com.autoria.clone.infrastructure.service.ProfanityFilterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ModerationServiceTest {

    @Autowired
    private ModerationService moderationService;

    @MockBean
    private AdvertisementRepository advertisementRepository;

    @MockBean
    private ProfanityFilterService profanityFilterService;

    @MockBean
    private EmailService emailService;

    private Advertisement advertisement;

    @BeforeEach
    public void setUp() {
        User user = new User();
        user.setId(1L);
        advertisement = new Advertisement();
        advertisement.setId(1L);
        advertisement.setUser(user);
        advertisement.setCarBrand(CarBrand.BMW);
        advertisement.setCarModel(CarModel.X5);
        advertisement.setPrice(new BigDecimal("35000"));
        advertisement.setOriginalCurrency("USD");
        advertisement.setCity("Kyiv");
        advertisement.setRegion("Kyiv");
        advertisement.setDescription("Good car");
        advertisement.setEditAttempts(0);

        when(advertisementRepository.findById(1L)).thenReturn(java.util.Optional.of(advertisement));
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(advertisement);
    }

    @Test
    public void testCheckAdvertisementNoProfanity() {
        when(profanityFilterService.containsProfanity("Good car")).thenReturn(false);

        boolean result = moderationService.checkAdvertisement(1L);

        assertTrue(result);
        assertEquals("ACTIVE", advertisement.getStatus());
        assertEquals(0, advertisement.getEditAttempts());
        verify(advertisementRepository, times(1)).save(advertisement);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testCheckAdvertisementWithProfanityFirstAttempt() {
        when(profanityFilterService.containsProfanity("Bad word")).thenReturn(true);

        boolean result = moderationService.checkAdvertisement(1L);

        assertFalse(result);
        assertEquals("PENDING", advertisement.getStatus());
        assertEquals(1, advertisement.getEditAttempts());
        verify(advertisementRepository, times(1)).save(advertisement);
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void testCheckAdvertisementWithProfanityThirdAttempt() {
        advertisement.setEditAttempts(2); // Устанавливаем 2 попытки перед тестом
        when(profanityFilterService.containsProfanity("Bad word")).thenReturn(true);

        boolean result = moderationService.checkAdvertisement(1L);

        assertFalse(result);
        assertEquals("INACTIVE", advertisement.getStatus());
        assertEquals(3, advertisement.getEditAttempts());
        verify(advertisementRepository, times(1)).save(advertisement);
        verify(emailService, times(1)).sendEmail("manager@autoria.clone", "Оголошення потребує перевірки", anyString());
    }

    @Test
    public void testCheckAdvertisementNotFound() {
        when(advertisementRepository.findById(999L)).thenReturn(java.util.Optional.empty());

        try {
            moderationService.checkAdvertisement(999L);
        } catch (IllegalArgumentException e) {
            assertEquals("Оголошення з ID 999 не знайдено", e.getMessage());
        }
        verify(advertisementRepository, never()).save(any(Advertisement.class));
        verify(emailService, never()).sendEmail(anyString(), anyString(), anyString());
    }
}