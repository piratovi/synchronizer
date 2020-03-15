package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.AbstractSync;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncRepository extends JpaRepository<AbstractSync, Integer> {
}
