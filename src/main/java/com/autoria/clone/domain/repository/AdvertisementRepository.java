package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement> {

    Long countByUserId(Long userId);

    @Query("SELECT AVG(a.price) FROM Advertisement a WHERE a.carBrand = :carBrand AND a.carModel = :carModel AND a.city = :city AND a.status = 'ACTIVE'")
    BigDecimal findAveragePriceByCarBrandAndCarModelAndCity(
            @Param("carBrand") CarBrand carBrand,
            @Param("carModel") CarModel carModel,
            @Param("city") String city);

    @Query("SELECT AVG(a.price) FROM Advertisement a WHERE a.carBrand = :carBrand AND a.carModel = :carModel AND a.region = :region AND a.status = 'ACTIVE'")
    BigDecimal findAveragePriceByCarBrandAndCarModelAndRegion(
            @Param("carBrand") CarBrand carBrand,
            @Param("carModel") CarModel carModel,
            @Param("region") String region);

    @Query("SELECT AVG(a.price) FROM Advertisement a WHERE a.carBrand = :carBrand AND a.carModel = :carModel AND a.status = 'ACTIVE'")
    BigDecimal findAveragePriceByCarBrandAndCarModel(
            @Param("carBrand") CarBrand carBrand,
            @Param("carModel") CarModel carModel);

    @Query("SELECT COUNT(v) FROM ViewLog v WHERE v.advertisement.id = :adId AND v.viewDate >= :#{T(java.time.LocalDateTime).now().minusDays(#days)}")
    long countViewsByAdIdAndDate(@Param("adId") Long adId, @Param("days") int days);
}