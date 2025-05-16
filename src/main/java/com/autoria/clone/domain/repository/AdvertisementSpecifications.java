package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Advertisement;
import com.autoria.clone.domain.enums.CarBrand;
import com.autoria.clone.domain.enums.CarModel;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AdvertisementSpecifications {

    public static Specification<Advertisement> search(
            CarBrand carBrand, CarModel carModel, BigDecimal minPrice, BigDecimal maxPrice,
            String city, String region, String currency) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();


            if (carBrand != null) {
                predicates.add(criteriaBuilder.equal(root.get("carBrand"), carBrand));
            }


            if (carModel != null) {
                predicates.add(criteriaBuilder.equal(root.get("carModel"), carModel));
            }


            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }


            if (city != null && !city.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("city"), city));
            }


            if (region != null && !region.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("region"), region));
            }


            if (currency != null && !currency.isEmpty()) {
                predicates.add(criteriaBuilder.isNotNull(root.get("convertedPrices").get(currency)));
            }

            predicates.add(criteriaBuilder.equal(root.get("status"), "ACTIVE"));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}