package com.autoria.clone.application.service;


import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.entity.ViewLog;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import com.autoria.clone.domain.repository.AdvertisementRepository;
import com.autoria.clone.domain.repository.AdvertisementSpecifications;
import com.autoria.clone.domain.repository.UserRepository;
import com.autoria.clone.domain.repository.ViewLogRepository;
import com.autoria.clone.infrastructure.exception.ProfanityException;
import com.autoria.clone.infrastructure.service.CurrencyService;
import com.autoria.clone.infrastructure.service.EmailService;
import com.autoria.clone.infrastructure.service.ProfanityFilterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final UserRepository userRepository;
    private final ViewLogRepository viewLogRepository;
    private final CurrencyService currencyService;
    private final EmailService emailService;
    private final ProfanityFilterService profanityFilterService;
    private final ModerationService moderationService;
    private static final Logger logger = LoggerFactory.getLogger(AdvertisementService.class);
    @Transactional
    public Advertisement createAdvertisement(User user, Advertisement advertisement) {
        logger.debug("Creating advertisement for user ID: {}", user.getId());
        if (!user.isPremium() && advertisementRepository.countByUserId(user.getId()) >= 1) {
            throw new IllegalStateException("Базовый аккаунт может создать лишь одно объявление");
        }

        Map<String, BigDecimal> rates = currencyService.getExchangeRates(advertisement.getOriginalCurrency(), "EUR");
        Map<String, BigDecimal> convertedPrices = calculateConvertedPrices(
                advertisement.getPrice(), advertisement.getOriginalCurrency(), rates);
        advertisement.setConvertedPrices(convertedPrices);
        advertisement.setExchangeRateDate(new Date());
        advertisement.setExchangeRateSource("PrivatBank");

        advertisement.setUser(user);
        advertisement.setStatus("PENDING");
        advertisement.setEditAttempts(0); // Инициализация edit_attempts
        logger.debug("Saving advertisement before moderation check");
        Advertisement saved = advertisementRepository.save(advertisement);
        logger.debug("Saved advertisement with ID: {}", saved.getId());

        logger.debug("Checking advertisement with ID: {}", saved.getId());
        if (!moderationService.checkAdvertisement(saved.getId())) {
            saved.setStatus("PENDING");
            logger.debug("Advertisement failed moderation, status set to PENDING");
            return advertisementRepository.save(saved);
        }

        saved.setStatus("ACTIVE");
        logger.debug("Advertisement passed moderation, status set to ACTIVE");
        saved = advertisementRepository.save(saved);
        user.setAdvertisementCount(user.getAdvertisementCount() + 1);
        userRepository.save(user);
        return saved;
    }

    @Transactional
    public Advertisement updateAdvertisement(Long id, Advertisement updatedAd) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Объявление не найдено"));
        if (advertisement.getEditAttempts() >= 3) {
            throw new IllegalStateException("Превышено количество попыток редактирования");
        }
        advertisement.setEditAttempts(advertisement.getEditAttempts() + 1);
        try {
            profanityFilterService.containsProfanity(updatedAd.getDescription());
            advertisement.setStatus("ACTIVE");
        } catch (ProfanityException e) {
            if (advertisement.getEditAttempts() >= 3) {
                notifyManager(advertisement);
                advertisement.setStatus("INACTIVE");
            } else {
                advertisement.setStatus("PENDING");
            }
        }
        advertisement.setDescription(updatedAd.getDescription());
        return advertisementRepository.save(advertisement);
    }

    @Transactional(readOnly = true)
    public Page<Advertisement> searchAdvertisements(
            CarBrand carBrand, CarModel carModel, BigDecimal minPrice, BigDecimal maxPrice,
            String city, String region, String currency, Pageable pageable) {
        return advertisementRepository.findAll(
                AdvertisementSpecifications.search(carBrand, carModel, minPrice, maxPrice, city, region, currency),
                pageable);
    }

    @Transactional
    public Advertisement getAdvertisementById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с ID " + id + " не найдено"));


        advertisement.setViews(advertisement.getViews() + 1);
        advertisementRepository.save(advertisement);


        ViewLog viewLog = new ViewLog();
        viewLog.setAdvertisement(advertisement);
        viewLog.setViewDate(LocalDateTime.now());
        viewLogRepository.save(viewLog);

        return advertisement;
    }

    public User loadUserEntity(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    @Transactional
    public Map<String, Object> getAdvertisementStats(Long adId, User user) {
        if (!user.isPremium()) {
            throw new AccessDeniedException("Только премиум-аккаунты могут просматривать статистику");
        }
        Advertisement ad = getAdvertisementById(adId);
        logger.debug("Fetching stats for advertisement ID: {}, carBrand: {}, carModel: {}",
                adId, ad.getCarBrand(), ad.getCarModel());
        Map<String, Object> stats = new HashMap<>();
        stats.put("views", ad.getViews());
        stats.put("viewsDaily", advertisementRepository.countViewsByAdIdAndDate(adId, 1));
        stats.put("viewsWeekly", advertisementRepository.countViewsByAdIdAndDate(adId, 7));
        stats.put("viewsMonthly", advertisementRepository.countViewsByAdIdAndDate(adId, 30));
        stats.put("avgPriceCity", advertisementRepository.findAveragePriceByCarBrandAndCarModelAndCity(
                ad.getCarBrand(), ad.getCarModel(), ad.getCity()));
        stats.put("avgPriceRegion", advertisementRepository.findAveragePriceByCarBrandAndCarModelAndRegion(
                ad.getCarBrand(), ad.getCarModel(), ad.getRegion()));
        stats.put("avgPriceUkraine", advertisementRepository.findAveragePriceByCarBrandAndCarModel(
                ad.getCarBrand(), ad.getCarModel()));
        return stats;
    }




    private Map<String, BigDecimal> calculateConvertedPrices(BigDecimal originalPrice, String originalCurrency, Map<String, BigDecimal> rates) {
        Map<String, BigDecimal> convertedPrices = new HashMap<>();
        convertedPrices.put(originalCurrency, originalPrice);

        if ("UAH".equals(originalCurrency)) {
            rates.forEach((currency, rate) -> {
                BigDecimal convertedPrice = originalPrice.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
                convertedPrices.put(currency, convertedPrice);
            });
        } else {
            BigDecimal uahRate = rates.get(originalCurrency);
            if (uahRate != null) {
                BigDecimal priceInUah = originalPrice.multiply(uahRate);
                convertedPrices.put("UAH", priceInUah);
                rates.forEach((currency, rate) -> {
                    if (!currency.equals(originalCurrency)) {
                        BigDecimal convertedPrice = priceInUah.divide(rate, 2, BigDecimal.ROUND_HALF_UP);
                        convertedPrices.put(currency, convertedPrice);
                    }
                });
            }
        }
        return convertedPrices;
    }

    private void notifyManager(Advertisement ad) {
        emailService.sendEmail("manager@example.com", "Перевірка оголошення #" + ad.getId(),
                "Оголошення потребує перевірки через нецензурну лексику.");
    }
}