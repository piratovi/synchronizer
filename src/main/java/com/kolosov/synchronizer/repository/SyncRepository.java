package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.domain.RootFolderSync;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SyncRepository extends JpaRepository<AbstractSync, Integer> {
    List<RootFolderSync> findAllRootFolderSyncs();
    List<FolderSync> findAllByParentNull();

}
