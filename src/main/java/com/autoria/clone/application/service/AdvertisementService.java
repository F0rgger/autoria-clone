package com.autoria.clone.application.service;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.entity.User;
import com.autoria.clone.domain.repository.AdvertisementRepository;
import com.autoria.clone.domain.repository.AdvertisementSpecifications;
import com.autoria.clone.infrastructure.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для управления объявлениями.
 */
@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final CurrencyService currencyService;

    /**
     * Создает новое объявление.
     *
     * @param user Пользователь, создающий объявление
     * @param advertisement Объявление
     * @return Сохраненное объявление
     */
    @Transactional
    public Advertisement createAdvertisement(User user, Advertisement advertisement) {
        if (!user.isPremium() && advertisementRepository.countByUserId(user.getId()) >= 1) {
            throw new IllegalStateException("Базовый аккаунт может создать лишь одно объявление");
        }

        // Получение курсов валют
        Map<String, BigDecimal> rates = currencyService.getExchangeRates("USD", "EUR");
        Map<String, BigDecimal> convertedPrices = calculateConvertedPrices(
                advertisement.getPrice(), advertisement.getOriginalCurrency(), rates);

        advertisement.setUser(user);
        advertisement.setConvertedPrices(convertedPrices);
        advertisement.setActive(true);
        return advertisementRepository.save(advertisement);
    }

    /**
     * Ищет объявления по заданным параметрам.
     *
     * @param carBrand Марка автомобиля
     * @param carModel Модель автомобиля
     * @param minPrice Минимальная цена
     * @param maxPrice Максимальная цена
     * @param city Город
     * @param region Регион
     * @param currency Валюта
     * @param pageable Пагинация
     * @return Страница с объявлениями
     */
    @Transactional(readOnly = true)
    public Page<Advertisement> searchAdvertisements(
            String carBrand, String carModel, BigDecimal minPrice, BigDecimal maxPrice,
            String city, String region, String currency, Pageable pageable) {
        return advertisementRepository.findAll(
                AdvertisementSpecifications.search(carBrand, carModel, minPrice, maxPrice, city, region, currency),
                pageable);
    }

    /**
     * Получает объявление по ID.
     *
     * @param id ID объявления
     * @return Объявление
     */
    @Transactional(readOnly = true)
    public Advertisement getAdvertisementById(Long id) {
        return advertisementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Объявление с ID " + id + " не найдено"));
    }

    /**
     * Рассчитывает цены в разных валютах.
     *
     * @param originalPrice Цена в оригинальной валюте
     * @param originalCurrency Оригинальная валюта
     * @param rates Курсы валют
     * @return Карта с конвертированными ценами
     */
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
}