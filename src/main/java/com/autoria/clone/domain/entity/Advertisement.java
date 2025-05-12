package com.autoria.clone.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

@Entity
@Table(name = "advertisements")
@Data
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String carBrand;

    @Column(nullable = false)
    private String carModel;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private String originalCurrency;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private String region;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false)
    private Integer editAttempts = 0;

    @ElementCollection
    @CollectionTable(name = "advertisement_converted_prices", joinColumns = @JoinColumn(name = "advertisement_id"))
    @MapKeyColumn(name = "currency")
    @Column(name = "price")
    private Map<String, BigDecimal> convertedPrices;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "dealership_id")
    private Dealership dealership;
}