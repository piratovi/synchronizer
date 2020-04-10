package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.RootFolderSync;
import com.kolosov.synchronizer.domain.Sync;
import com.kolosov.synchronizer.domain.FolderSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncRepository extends JpaRepository<Sync, Integer> {
}
