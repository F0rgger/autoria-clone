//package com.autoria.clone.application.service;
//
//import com.autoria.clone.domain.repository.AdvertisementRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//
///**
// * Сервис для аналитики объявлений.
// */
//@Service
//@RequiredArgsConstructor
//public class AnalyticsService {
//
//    private final AdvertisementRepository advertisementRepository;
//
//    /**
//     * Получает среднюю цену по марке, модели и городу.
//     *
//     * @param carBrand Марка автомобиля
//     * @param carModel Модель автомобиля
//     * @param city Город
//     * @return Средняя цена
//     */
//    public BigDecimal getAveragePriceByCity(String carBrand, String carModel, String city) {
//        BigDecimal averagePrice = advertisementRepository.findAveragePriceByCarBrandAndCarModelAndCity(carBrand, carModel, city);
//        return averagePrice != null ? averagePrice : BigDecimal.ZERO;
//    }
//
//    /**
//     * Получает среднюю цену по марке, модели и региону.
//     *
//     * @param carBrand Марка автомобиля
//     * @param carModel Модель автомобиля
//     * @param region Регион
//     * @return Средняя цена
//     */
//    public BigDecimal getAveragePriceByRegion(String carBrand, String carModel, String region) {
//        BigDecimal averagePrice = advertisementRepository.findAveragePriceByCarBrandAndCarModelAndRegion(carBrand, carModel, region);
//        return averagePrice != null ? averagePrice : BigDecimal.ZERO;
//    }
//
//    /**
//     * Получает среднюю цену по марке и модели.
//     *
//     * @param carBrand Марка автомобиля
//     * @param carModel Модель автомобиля
//     * @return Средняя цена
//     */
//    public BigDecimal getAveragePriceByBrandAndModel(String carBrand, String carModel) {
//        BigDecimal averagePrice = advertisementRepository.findAveragePriceByCarBrandAndCarModel(carBrand, carModel);
//        return averagePrice != null ? averagePrice : BigDecimal.ZERO;
//    }
//}