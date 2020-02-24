package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.HistorySync;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorySyncRepository extends JpaRepository<HistorySync, Long> {
}
