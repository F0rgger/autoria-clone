package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Advertisement;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Спецификации для динамического поиска объявлений.
 */
public class AdvertisementSpecifications {

    public static Specification<Advertisement> search(
            String carBrand, String carModel, BigDecimal minPrice, BigDecimal maxPrice,
            String city, String region, String currency) {
        return (Root<Advertisement> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (carBrand != null && !carBrand.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("carBrand"), carBrand));
            }
            if (carModel != null && !carModel.isEmpty()) {
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
                predicates.add(criteriaBuilder.equal(root.get("originalCurrency"), currency));
            }
            predicates.add(criteriaBuilder.isTrue(root.get("active")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}