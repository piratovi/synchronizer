package com.kolosov.synchronizer.repository;

import com.kolosov.synchronizer.domain.AbstractSync;
import com.kolosov.synchronizer.domain.FolderSync;
import com.kolosov.synchronizer.enums.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface FolderSyncRepository extends JpaRepository<FolderSync, Long> {

//    List<FolderSync> findAllByLocation(Location location);

//    @Transactional
//    void deleteAllByLocation(Location location);
}
