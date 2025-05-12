package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.Dealership;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторій для автосалонів.
 */
public interface DealershipRepository extends JpaRepository<Dealership, Long> {
}