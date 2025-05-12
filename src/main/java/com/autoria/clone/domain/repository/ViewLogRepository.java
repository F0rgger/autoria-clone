package com.autoria.clone.domain.repository;

import com.autoria.clone.domain.entity.ViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ViewLogRepository extends JpaRepository<ViewLog, Long> {

    Long countByAdvertisementId(Long advertisementId);

    @Query("SELECT COUNT(v) FROM ViewLog v WHERE v.advertisementId = :advertisementId AND v.timestamp > :since")
    Long countByAdvertisementIdAndTimestampAfter(Long advertisementId, LocalDateTime since);
}