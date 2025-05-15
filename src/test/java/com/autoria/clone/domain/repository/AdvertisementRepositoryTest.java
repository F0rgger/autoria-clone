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

@DataJpaTest
public class AdvertisementRepositoryTest {

    @Autowired
    private AdvertisementRepository advertisementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViewLogRepository viewLogRepository;

    @Test
    public void testCountByUserId() {
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
        advertisementRepository.save(ad);

        Long count = advertisementRepository.countByUserId(user.getId());
        assertEquals(1, count);
    }

    @Test
    public void testFindAveragePriceByCarBrandAndCarModelAndCity() {
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
        advertisementRepository.save(ad);

        BigDecimal avgPrice = advertisementRepository.findAveragePriceByCarBrandAndCarModelAndCity(
                CarBrand.BMW.name(), CarModel.X5.name(), "Kyiv");
        assertEquals(new BigDecimal("35000"), avgPrice);
    }

    @Test
    public void testCountViewsByAdIdAndDate() {
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
        viewLogRepository.save(viewLog);

        long count = advertisementRepository.countViewsByAdIdAndDate(ad.getId(), 1);
        assertEquals(1, count);
    }
}