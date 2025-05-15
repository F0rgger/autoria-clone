package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.entity.ViewLog;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ViewLogRepositoryTest {

    @Autowired
    private ViewLogRepository viewLogRepository;

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveAndFindById() {
        User user = new User();
        user.setEmail("user@example.com");
        user = userRepository.save(user);

        Advertisement ad = new Advertisement();
        ad.setUser(user);
        ad.setCarBrand(CarBrand.BMW);
        ad.setCarModel(CarModel.X5);
        ad.setPrice(new BigDecimal("35000"));
        ad.setOriginalCurrency("USD");
        ad.setCity("Kyiv");
        ad.setRegion("Kyiv");
        ad.setStatus("ACTIVE");
        ad = advertisementRepository.save(ad);

        ViewLog viewLog = new ViewLog();
        viewLog.setAdvertisement(ad);
        viewLog.setViewDate(LocalDateTime.now());
        viewLog = viewLogRepository.save(viewLog);

        var found = viewLogRepository.findById(viewLog.getId());
        assertTrue(found.isPresent());
        assertEquals(ad.getId(), found.get().getAdvertisement().getId());
    }
}