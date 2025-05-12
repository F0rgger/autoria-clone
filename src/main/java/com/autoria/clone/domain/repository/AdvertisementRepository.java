package com.autoria.clone.domain.repository;


import com.autoria.clone.domain.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement> {

    Long countByUserId(Long userId);

    @Query("SELECT AVG(a.price) FROM Advertisement a WHERE a.carBrand = :carBrand AND a.carModel = :carModel AND a.city = :city AND a.active = true")
    BigDecimal findAveragePriceByCarBrandAndCarModelAndCity(
            @Param("carBrand") String carBrand,
            @Param("carModel") String carModel,
            @Param("city") String city);

    @Query("SELECT AVG(a.price) FROM Advertisement a WHERE a.carBrand = :carBrand AND a.carModel = :carModel AND a.region = :region AND a.active = true")
    BigDecimal findAveragePriceByCarBrandAndCarModelAndRegion(
            @Param("carBrand") String carBrand,
            @Param("carModel") String carModel,
            @Param("region") String region);

    @Query("SELECT AVG(a.price) FROM Advertisement a WHERE a.carBrand = :carBrand AND a.carModel = :carModel AND a.active = true")
    BigDecimal findAveragePriceByCarBrandAndCarModel(
            @Param("carBrand") String carBrand,
            @Param("carModel") String carModel);
}