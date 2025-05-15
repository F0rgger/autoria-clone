package com.autoria.clone.application.dto;

import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class AdvertisementDTO {
    private Long id;
    private CarBrand carBrand;
    private CarModel carModel;
    private BigDecimal price;
    private String originalCurrency;
    private String city;
    private String region;
    private String description;
    private String status;
    private Integer editAttempts;
    private Map<String, BigDecimal> convertedPrices;
    private int views;
    private String exchangeRateSource;
    @NotNull(message = "User ID must not be null")
    private Long userId;
    private Long dealershipId;


    @JsonIgnore
    public Long getId() {
        return id;
    }
}