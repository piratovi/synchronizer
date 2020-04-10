package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.RootFolderSync;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RootFolderSyncRepository extends JpaRepository<RootFolderSync, Integer> {
}
