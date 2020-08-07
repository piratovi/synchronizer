package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.Sync;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface SyncRepository extends JpaRepository<Sync, Integer> {
}
