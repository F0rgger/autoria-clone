package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.AdvertisementRepository;
import com.autoria.clone.domain.repository.UserRepository;
import com.autoria.clone.infrastructure.exception.ProfanityException;
import com.autoria.clone.infrastructure.service.CurrencyService;
import com.autoria.clone.infrastructure.service.EmailService;
import com.autoria.clone.infrastructure.service.ProfanityFilterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AdvertisementServiceTest {

    @Autowired
    private AdvertisementService advertisementService;

    @MockBean
    private AdvertisementRepository advertisementRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private ProfanityFilterService profanityFilterService;

    @Test
    public void testCreateAdvertisementSuccess() {
        User user = new User();
        user.setPremium(true);
        user.setId(1L);
        Advertisement ad = new Advertisement();
        ad.setUser(user);
        ad.setPrice(new BigDecimal("35000"));
        ad.setOriginalCurrency("USD");

        Map<String, BigDecimal> rates = new HashMap<>();
        rates.put("EUR", new BigDecimal("0.03"));
        when(currencyService.getExchangeRates("USD", "EUR")).thenReturn(rates);
        when(profanityFilterService.containsProfanity(anyString())).thenReturn(false);
        when(advertisementRepository.countByUserId(1L)).thenReturn(0L);
        when(advertisementRepository.save(any(Advertisement.class))).thenReturn(ad);

        Advertisement result = advertisementService.createAdvertisement(user, ad);
        assertEquals("ACTIVE", result.getStatus());
        verify(userRepository).save(user);
    }

    @Test
    public void testCreateAdvertisementWithProfanity() {
        User user = new User();
        user.setPremium(true);
        Advertisement ad = new Advertisement();
        ad.setUser(user);

        when(profanityFilterService.containsProfanity(anyString())).thenThrow(new ProfanityException("Profanity detected"));
        when(advertisementRepository.countByUserId(anyLong())).thenReturn(0L);

        Advertisement result = advertisementService.createAdvertisement(user, ad);
        assertEquals("PENDING", result.getStatus());
        assertEquals(1, result.getEditAttempts());
    }
}