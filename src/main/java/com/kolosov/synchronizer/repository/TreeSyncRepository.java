package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.TreeSync;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeSyncRepository extends JpaRepository<TreeSync, Integer> {
}
