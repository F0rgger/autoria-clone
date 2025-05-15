package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {
}