package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.HistorySync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface HistorySyncRepository extends JpaRepository<HistorySync, Integer> {
}
